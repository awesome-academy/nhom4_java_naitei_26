package nhom4.public_service_management_system.activity_log;

import org.springframework.stereotype.Component;
import nhom4.public_service_management_system.activity_log.dto.ActivityLogRequest;
import nhom4.public_service_management_system.activity_log.dto.ActivityLogResponse;
import nhom4.public_service_management_system.user.UserEntity;

@Component
public class ActivityLogMapper {

    public ActivityLogResponse toResponse(ActivityLogEntity entity) {
        if (entity == null) {
            return null;
        }

        ActivityLogResponse response = new ActivityLogResponse();
        response.setId(entity.getId());
        response.setCreatedAt(entity.getCreatedAt());
        response.setAction(entity.getAction());
        response.setDescription(entity.getDescription());

        if (entity.getUser() != null) {
            response.setUserId(entity.getUser().getId());
            response.setUserEmail(entity.getUser().getEmail());
        }

        return response;
    }

    public ActivityLogEntity toEntity(ActivityLogRequest request, UserEntity user) {
        if (request == null) {
            return null;
        }

        ActivityLogEntity entity = new ActivityLogEntity();
        entity.setAction(request.getAction());
        entity.setDescription(request.getDescription());
        entity.setUser(user);

        return entity;
    }
}