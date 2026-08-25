package nhom4.public_service_management_system.activity_log;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nhom4.public_service_management_system.activity_log.dto.ActivityLogRequest;
import nhom4.public_service_management_system.activity_log.dto.ActivityLogResponse;
import nhom4.public_service_management_system.exception.ResourceNotFoundException;
import nhom4.public_service_management_system.user.UserEntity;
import nhom4.public_service_management_system.user.UserRepository;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogMapper activityLogMapper;
    private final UserRepository userRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository,
                              ActivityLogMapper activityLogMapper,
                              UserRepository userRepository) {
        this.activityLogRepository = activityLogRepository;
        this.activityLogMapper = activityLogMapper;
        this.userRepository = userRepository;
    }

    @Transactional
    public ActivityLogResponse create(ActivityLogRequest request) {
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        ActivityLogEntity entity = activityLogMapper.toEntity(request, user);
        ActivityLogEntity savedEntity = activityLogRepository.save(entity);

        return activityLogMapper.toResponse(savedEntity);
    }

    @Transactional(readOnly = true)
    public Page<ActivityLogResponse> findAll(Pageable pageable) {
        return activityLogRepository.findAll(pageable)
                .map(activityLogMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ActivityLogResponse findById(Long id) {
        ActivityLogEntity entity = activityLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity log not found with id: " + id));
        return activityLogMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<ActivityLogResponse> findByUserId(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return activityLogRepository.findByUserId(userId, pageable)
                .map(activityLogMapper::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        if (!activityLogRepository.existsById(id)) {
            throw new ResourceNotFoundException("Activity log not found with id: " + id);
        }
        activityLogRepository.deleteById(id);
    }

    @Transactional
    public void deleteOldLogs(LocalDateTime beforeDate) {
        activityLogRepository.deleteByCreatedAtBefore(beforeDate);
    }

    @Transactional
    public void logCurrentAction(String action, String description) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String currentEmail = auth.getName();
            userRepository.findByEmail(currentEmail).ifPresent(user -> {
                nhom4.public_service_management_system.activity_log.ActivityLogEntity entity = new nhom4.public_service_management_system.activity_log.ActivityLogEntity();
                entity.setAction(action);
                entity.setDescription(description);
                entity.setUser(user);
                activityLogRepository.save(entity);
            });
        }
    }
}