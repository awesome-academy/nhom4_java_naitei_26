package nhom4.public_service_management_system.service_required_document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ServiceRequiredDocumentRequest(
        @NotNull Long serviceId,
        @NotBlank String documentName,
        @NotNull Boolean required
) {
}
