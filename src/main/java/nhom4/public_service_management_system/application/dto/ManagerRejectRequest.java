package nhom4.public_service_management_system.application.dto;

import jakarta.validation.constraints.NotBlank;

public record ManagerRejectRequest(
        @NotBlank(message = "Lý do từ chối không được để trống")
        String rejectionReason
) {
}
