package nhom4.public_service_management_system.staff.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StaffRequest(
        @NotNull Long userId,
        @NotBlank String name,
        @Size(max = 20) String phone,
        String address,
        Long departmentId
) {
}
