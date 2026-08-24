package nhom4.public_service_management_system.service_required_document.dto;

public record ServiceRequiredDocumentResponse(
        Long id,
        Long serviceId,
        String documentName,
        Boolean required
) {
}
