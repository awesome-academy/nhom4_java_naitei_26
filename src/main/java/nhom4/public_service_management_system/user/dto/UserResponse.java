package nhom4.public_service_management_system.user.dto;

import nhom4.public_service_management_system.user.UserRole;
import nhom4.public_service_management_system.user.UserStatus;

public record UserResponse(
        Long id,
        String email,
        UserRole role,
        UserStatus status,
        Boolean emailNotificationEnabled
) {
}
