package nhom4.public_service_management_system.user.dto;

import nhom4.public_service_management_system.application.dto.ApplicationResponse;
import nhom4.public_service_management_system.enums.UserRole;
import nhom4.public_service_management_system.enums.UserStatus;
import nhom4.public_service_management_system.enums.Gender;

import java.time.LocalDate;
import java.util.List;

public record UserProfileResponse(
        Long id,
        long displayId,
        String name,
        String email,
        UserRole role,
        String phone,
        String address,
        LocalDate dateOfBirth,
        Gender gender,
        String identityNumber,
        UserStatus status,
        Boolean emailNotificationEnabled,
        List<ApplicationResponse> applications
) {
}
