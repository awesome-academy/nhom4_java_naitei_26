package nhom4.public_service_management_system.application;

import nhom4.public_service_management_system.application.dto.ApplicationRequest;
import nhom4.public_service_management_system.application.dto.ApplicationResponse;
import nhom4.public_service_management_system.application_history.ApplicationHistoryEntity;
import nhom4.public_service_management_system.application_history.ApplicationHistoryRepository;
import nhom4.public_service_management_system.enums.ApplicationStatus;
import nhom4.public_service_management_system.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private ApplicationHistoryRepository applicationHistoryRepository;

    // --- CÁC HÀM CŨ CỦA DỰ ÁN ---
    public ApplicationResponse create(ApplicationRequest request, Long userId) {
        ApplicationEntity entity = applicationMapper.toEntity(request);
        entity.setCitizenId(userId);
        entity.setStatus(ApplicationStatus.RECEIVED);
        entity.setAssignedStaffId(null);
        entity.setCompletedAt(null);
        entity.setResultNote(null);
        entity.setRejectionReason(null);

        ApplicationEntity savedEntity = applicationRepository.save(entity);
        return applicationMapper.toResponse(savedEntity);
    }

    public List<ApplicationResponse> getApplication(Long userId) {
        List<ApplicationResponse> applicationResponseList = new ArrayList<>();
        List<ApplicationEntity> applicationEntityList = applicationRepository.findAllByCitizenId(userId);
        for (ApplicationEntity applicationEntity : applicationEntityList) {
            applicationResponseList.add(applicationMapper.toResponse(applicationEntity)); // Sửa lỗi add thiếu của code cũ
        }
        return applicationResponseList;
    }

    public ApplicationResponse getById(Long id) {
        return applicationMapper.toResponse(applicationRepository.findById(id).get());
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

        return applicationMapper.toResponse(updatedApp);
    }
}