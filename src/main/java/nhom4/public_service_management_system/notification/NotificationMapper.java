package nhom4.public_service_management_system.notification;

import nhom4.public_service_management_system.notification.dto.NotificationResponse;

public class NotificationMapper {

    private NotificationMapper(){
    }

    public static NotificationResponse toResponse(NotificationEntity entity) {
        if(entity == null) {
            return null;
        }
        Long userId = entity.getUser() !=  null ? entity.getUser().getId() : null;
        Long applicationId = entity.getApplication() != null ? entity.getApplication().getId() : null;

        return new NotificationResponse(
                entity.getId(),
                userId,
                applicationId,
                entity.getMessage(),
                entity.isRead()
        );
    }
}