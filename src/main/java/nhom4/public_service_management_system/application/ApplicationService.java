package nhom4.public_service_management_system.application;

import nhom4.public_service_management_system.application.dto.ApplicationForm;
import nhom4.public_service_management_system.application.dto.ApplicationRequest;
import nhom4.public_service_management_system.application.dto.ApplicationResponse;
import nhom4.public_service_management_system.application_history.ApplicationHistoryEntity;
import nhom4.public_service_management_system.application_history.ApplicationHistoryRepository;
import nhom4.public_service_management_system.citizen.CitizenEntity;
import nhom4.public_service_management_system.citizen.CitizenRepository;
import nhom4.public_service_management_system.enums.ApplicationStatus;
import nhom4.public_service_management_system.exception.ResourceNotFoundException;
import nhom4.public_service_management_system.service.ServiceEntity;
import nhom4.public_service_management_system.service.ServiceRepository;
import nhom4.public_service_management_system.staff.StaffRepository;
import nhom4.public_service_management_system.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CitizenRepository citizenRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired(required = false)
    private ApplicationHistoryRepository applicationHistoryRepository;

    @Transactional
    public ApplicationResponse create(ApplicationRequest request, Long userId) {
        ApplicationEntity entity = applicationMapper.toEntity(request);
        
        CitizenEntity citizen = citizenRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin công dân cho tài khoản này"));
        entity.setCitizen(citizen);
        entity.setStatus(ApplicationStatus.RECEIVED);
        entity.setAssignedStaffId(null);
        entity.setCompletedAt(null);
        entity.setResultNote(null);
        entity.setRejectionReason(null);
        
        ApplicationEntity savedEntity = applicationRepository.save(entity);
        return applicationMapper.toResponse(savedEntity);
    }

    @Transactional
    public ApplicationResponse createFromForm(ApplicationForm form) {
        CitizenEntity citizen = citizenRepository.findById(form.getCitizenId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công dân với ID: " + form.getCitizenId()));

        ServiceEntity service = serviceRepository.findById(form.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ với ID: " + form.getServiceId()));

        ApplicationEntity entity = new ApplicationEntity();
        entity.setCitizen(citizen);
        entity.setServiceId(service.getId());
        entity.setStatus(form.getStatus() != null ? form.getStatus() : ApplicationStatus.RECEIVED);
        entity.setAssignedStaffId(form.getAssignedStaffId());
        entity.setData(form.toDataMap());
        entity.setResultNote(form.getResultNote());
        entity.setRejectionReason(form.getRejectionReason());

        if (entity.getStatus() == ApplicationStatus.APPROVED || entity.getStatus() == ApplicationStatus.REJECTED) {
            entity.setCompletedAt(LocalDateTime.now());
        }

        ApplicationEntity savedEntity = applicationRepository.save(entity);
        return applicationMapper.toResponse(savedEntity);
    }

    @Transactional
    public ApplicationResponse update(Long id, ApplicationForm form) {
        ApplicationEntity entity = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ với id: " + id));

        CitizenEntity citizen = citizenRepository.findById(form.getCitizenId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công dân với ID: " + form.getCitizenId()));

        serviceRepository.findById(form.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ với ID: " + form.getServiceId()));

        ApplicationStatus oldStatus = entity.getStatus();
        ApplicationStatus newStatus = form.getStatus() != null ? form.getStatus() : entity.getStatus();

        entity.setCitizen(citizen);
        entity.setServiceId(form.getServiceId());
        entity.setAssignedStaffId(form.getAssignedStaffId());
        entity.setStatus(newStatus);
        entity.setData(form.toDataMap());
        entity.setResultNote(form.getResultNote());
        entity.setRejectionReason(form.getRejectionReason());

        if (newStatus == ApplicationStatus.APPROVED || newStatus == ApplicationStatus.REJECTED) {
            if (entity.getCompletedAt() == null) {
                entity.setCompletedAt(LocalDateTime.now());
            }
        } else {
            entity.setCompletedAt(null);
        }

        if (oldStatus != newStatus && applicationHistoryRepository != null) {
            ApplicationHistoryEntity history = new ApplicationHistoryEntity();
            history.setApplication(entity);
            history.setOldStatus(oldStatus);
            history.setNewStatus(newStatus);
            history.setNote(form.getResultNote() != null ? form.getResultNote() : form.getRejectionReason());
            history.setChangedAt(LocalDateTime.now());
            applicationHistoryRepository.save(history);
        }

        ApplicationEntity updated = applicationRepository.save(entity);
        return applicationMapper.toResponse(updated);
    }

    @Transactional
    public ApplicationResponse updateStatus(Long id, ApplicationStatus newStatus, String resultNote, String rejectionReason, UserEntity currentUser) {
        ApplicationEntity entity = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ với id: " + id));

        ApplicationStatus oldStatus = entity.getStatus();
        entity.setStatus(newStatus);

        if (resultNote != null && !resultNote.isBlank()) {
            entity.setResultNote(resultNote);
        }
        if (rejectionReason != null && !rejectionReason.isBlank()) {
            entity.setRejectionReason(rejectionReason);
        }

        if (newStatus == ApplicationStatus.APPROVED || newStatus == ApplicationStatus.REJECTED) {
            entity.setCompletedAt(LocalDateTime.now());
        } else {
            entity.setCompletedAt(null);
        }

        if (applicationHistoryRepository != null) {
            ApplicationHistoryEntity history = new ApplicationHistoryEntity();
            history.setApplication(entity);
            history.setOldStatus(oldStatus);
            history.setNewStatus(newStatus);
            history.setChangedBy(currentUser);
            history.setChangedAt(LocalDateTime.now());
            history.setNote(newStatus == ApplicationStatus.REJECTED ? rejectionReason : resultNote);
            applicationHistoryRepository.save(history);
        }

        ApplicationEntity updated = applicationRepository.save(entity);
        return applicationMapper.toResponse(updated);
    }

    @Transactional
    public ApplicationResponse assignStaff(Long id, Long staffId) {
        ApplicationEntity entity = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ với id: " + id));

        if (staffId != null) {
            staffRepository.findById(staffId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cán bộ với id: " + staffId));
        }

        entity.setAssignedStaffId(staffId);
        ApplicationEntity updated = applicationRepository.save(entity);
        return applicationMapper.toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!applicationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy hồ sơ với id: " + id);
        }
        applicationRepository.deleteById(id);
    }

    public Page<ApplicationResponse> getAll(Pageable pageable) {
        return applicationRepository.findAll(pageable)
                .map(applicationMapper::toResponse);
    }

    public Page<ApplicationResponse> search(String keyword, ApplicationStatus status, Pageable pageable) {
        if ((keyword == null || keyword.isBlank()) && status == null) {
            return getAll(pageable);
        }
        return applicationRepository.searchApplications(keyword, status, pageable)
                .map(applicationMapper::toResponse);
    }

    public List<ApplicationResponse> getApplication(Long userId) {
        List<ApplicationEntity> applicationEntityList = applicationRepository.findByCitizenUserId(userId);
        return applicationEntityList.stream()
                .map(applicationMapper::toResponse)
                .toList();
    }

    public ApplicationResponse getById(Long id) {
        ApplicationEntity entity = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ với id: " + id));
        return applicationMapper.toResponse(entity);
    }

    public ApplicationEntity getEntityById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ với id: " + id));
    }

    public long countByStatus(ApplicationStatus status) {
        if (status == null) {
            return applicationRepository.count();
        }
        return applicationRepository.countByStatus(status);
    }
}
