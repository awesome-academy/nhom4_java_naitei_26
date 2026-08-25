package nhom4.public_service_management_system.application.dto;

import jakarta.validation.constraints.NotNull;

public record ManagerTransferRequest(
        @NotNull(message = "Staff ID không được để trống")
        Long staffId
) {
}
