package nhom4.public_service_management_system.citizen.dto;

import nhom4.public_service_management_system.enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Response DTO
public record CitizenResponse(
        Long id,
        Long userId,
        String name,
        LocalDate dateOfBirth,
        Gender gender,
        String identityNumber,
        String address,
        String phone,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
