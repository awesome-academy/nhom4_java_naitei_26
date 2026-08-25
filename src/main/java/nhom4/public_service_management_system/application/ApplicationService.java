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
import nhom4.public_service_management_system.activity_log.ActivityLogService;

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

    @Autowired
    private ActivityLogService activityLogService;

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

        activityLogService.logCurrentAction("CREATE", "Công dân nộp hồ sơ mới ID: " + savedEntity.getId());

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

        activityLogService.logCurrentAction("CREATE", "Nộp hồ sơ (từ form) ID: " + savedEntity.getId());

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

        activityLogService.logCurrentAction("UPDATE", "Cập nhật hồ sơ ID: " + id);

        return applicationMapper.toResponse(updated);
    }

    @Transactional
    public ApplicationResponse updateStatus(Long id, ApplicationStatus newStatus, String resultNote, String rejectionReason, UserEntity currentUser) {
        ApplicationEntity entity = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ với id: " + id));

        ApplicationStatus oldStatus = entity.getStatus() != null
                ? entity.getStatus()
                : ApplicationStatus.RECEIVED;
        entity.setStatus(oldStatus);
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

        if (applicationHistoryRepository != null && oldStatus != newStatus) {
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

        activityLogService.logCurrentAction("UPDATE_STATUS", "Cập nhật trạng thái hồ sơ ID: " + id + " thành " + newStatus);

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

        activityLogService.logCurrentAction("ASSIGN_STAFF", "Phân công hồ sơ ID: " + id + " cho cán bộ ID: " + staffId);

        return applicationMapper.toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!applicationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy hồ sơ với id: " + id);
        }
        applicationRepository.deleteById(id);

        activityLogService.logCurrentAction("DELETE", "Xóa hồ sơ ID: " + id);
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

    public Page<ApplicationResponse> getApplicationsForStaff(Long staffId, Pageable pageable) {
        Page<ApplicationEntity> entities = applicationRepository.findByAssignedStaffId(staffId, pageable);
        return entities.map(applicationMapper::toResponse);
    }

    public ApplicationResponse getApplicationDetailForStaff(Long appId, Long staffId) {
        ApplicationEntity application = applicationRepository.findByIdAndAssignedStaffId(appId, staffId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ hoặc bạn không có quyền truy cập"));
        return applicationMapper.toResponse(application);
    }

    @Transactional
    public ApplicationResponse updateStatusToProcessing(Long appId, Long staffId, Long userId) {
        ApplicationEntity application = applicationRepository.findByIdAndAssignedStaffId(appId, staffId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ hoặc bạn không có quyền xử lý"));

        if (application.getStatus() != ApplicationStatus.RECEIVED) {
            throw new IllegalStateException("Hồ sơ phải ở trạng thái RECEIVED mới có thể chuyển sang PROCESSING");
        }

        ApplicationHistoryEntity history = new ApplicationHistoryEntity();
        history.setApplication(application);
        history.setOldStatus(application.getStatus());
        history.setNewStatus(ApplicationStatus.PROCESSING);

        UserEntity user = new UserEntity();
        user.setId(userId);
        history.setChangedBy(user);

        history.setChangedAt(LocalDateTime.now());
        history.setNote("Cán bộ tiếp nhận và bắt đầu xử lý hồ sơ");

        applicationHistoryRepository.save(history);

        application.setStatus(ApplicationStatus.PROCESSING);
        ApplicationEntity updatedApp = applicationRepository.save(application);

        activityLogService.logCurrentAction("UPDATE_STATUS", "Cán bộ ID: " + staffId + " tiếp nhận hồ sơ ID: " + appId);

        return applicationMapper.toResponse(updatedApp);
    }

    // ─── Manager methods ──────────────────────────────────────────────────────

    @Transactional
    public ApplicationResponse transferToStaff(Long appId, Long newStaffId, Long managerId) {
        ApplicationEntity application = applicationRepository.findById(appId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ với id: " + appId));

        staffRepository.findById(newStaffId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cán bộ với id: " + newStaffId));

        Long oldStaffId = application.getAssignedStaffId();
        application.setAssignedStaffId(newStaffId);

        if (applicationHistoryRepository != null) {
            ApplicationHistoryEntity history = new ApplicationHistoryEntity();
            history.setApplication(application);
            history.setOldStatus(application.getStatus());
            history.setNewStatus(application.getStatus());
            UserEntity manager = new UserEntity();
            manager.setId(managerId);
            history.setChangedBy(manager);
            history.setChangedAt(LocalDateTime.now());
            history.setNote("Manager chuyển hồ sơ từ staff#" + oldStaffId + " sang staff#" + newStaffId);
            applicationHistoryRepository.save(history);
        }

        ApplicationEntity updated = applicationRepository.save(application);

        activityLogService.logCurrentAction("ASSIGN_STAFF", "Manager chuyển hồ sơ ID: " + appId + " sang cán bộ ID: " + newStaffId);

        return applicationMapper.toResponse(updated);
    }

    @Transactional
    public ApplicationResponse approveApplication(Long appId, String resultNote, Long managerId) {
        ApplicationEntity application = applicationRepository.findById(appId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ với id: " + appId));

        if (application.getStatus() != ApplicationStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Chỉ có thể duyệt hồ sơ đang ở trạng thái PROCESSING. Trạng thái hiện tại: "
                            + application.getStatus());
        }

        ApplicationStatus oldStatus = application.getStatus();
        application.setStatus(ApplicationStatus.APPROVED);
        application.setCompletedAt(LocalDateTime.now());
        if (resultNote != null && !resultNote.isBlank()) {
            application.setResultNote(resultNote);
        }

        if (applicationHistoryRepository != null) {
            ApplicationHistoryEntity history = new ApplicationHistoryEntity();
            history.setApplication(application);
            history.setOldStatus(oldStatus);
            history.setNewStatus(ApplicationStatus.APPROVED);
            UserEntity manager = new UserEntity();
            manager.setId(managerId);
            history.setChangedBy(manager);
            history.setChangedAt(LocalDateTime.now());
            history.setNote(resultNote != null ? resultNote : "Manager phê duyệt hồ sơ");
            applicationHistoryRepository.save(history);
        }

        ApplicationEntity updated = applicationRepository.save(application);

        activityLogService.logCurrentAction("UPDATE_STATUS", "Manager phê duyệt hồ sơ ID: " + appId);

        return applicationMapper.toResponse(updated);
    }

    @Transactional
    public ApplicationResponse rejectApplication(Long appId, String rejectionReason, Long managerId) {
        ApplicationEntity application = applicationRepository.findById(appId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ với id: " + appId));

        if (application.getStatus() != ApplicationStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Chỉ có thể từ chối hồ sơ đang ở trạng thái PROCESSING. Trạng thái hiện tại: "
                            + application.getStatus());
        }

        ApplicationStatus oldStatus = application.getStatus();
        application.setStatus(ApplicationStatus.REJECTED);
        application.setCompletedAt(LocalDateTime.now());
        application.setRejectionReason(rejectionReason);

        if (applicationHistoryRepository != null) {
            ApplicationHistoryEntity history = new ApplicationHistoryEntity();
            history.setApplication(application);
            history.setOldStatus(oldStatus);
            history.setNewStatus(ApplicationStatus.REJECTED);
            UserEntity manager = new UserEntity();
            manager.setId(managerId);
            history.setChangedBy(manager);
            history.setChangedAt(LocalDateTime.now());
            history.setNote(rejectionReason);
            applicationHistoryRepository.save(history);
        }

        ApplicationEntity updated = applicationRepository.save(application);

        activityLogService.logCurrentAction("UPDATE_STATUS", "Manager từ chối hồ sơ ID: " + appId);

        return applicationMapper.toResponse(updated);
    }
}