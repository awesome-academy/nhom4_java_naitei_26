package nhom4.public_service_management_system.notification.dto;

public record NotificationResponse(
        Long id,
        Long userId,
        Long applicationId,
        String message,
        boolean isRead
) {
}