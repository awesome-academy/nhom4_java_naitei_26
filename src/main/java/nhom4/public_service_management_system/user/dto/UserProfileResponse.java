package nhom4.public_service_management_system.user.dto;

import nhom4.public_service_management_system.enums.UserRole;
import nhom4.public_service_management_system.enums.UserStatus;

public record UserProfileResponse(
        Long id,
        long displayId,
        String name,
        String email,
        UserRole role,
        String phone,
        String address,
        UserStatus status,
        Boolean emailNotificationEnabled
) {
}
