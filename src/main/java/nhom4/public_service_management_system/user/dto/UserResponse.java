package nhom4.public_service_management_system.user.dto;

import nhom4.public_service_management_system.enums.UserRole;
import nhom4.public_service_management_system.enums.UserStatus;

public record UserResponse(
        Long id,
        String email,
        UserRole role,
        UserStatus status,
        Boolean emailNotificationEnabled
) {
}
