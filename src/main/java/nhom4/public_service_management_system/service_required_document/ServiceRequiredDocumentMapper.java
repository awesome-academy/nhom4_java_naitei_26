package nhom4.public_service_management_system.service_required_document;

import org.springframework.stereotype.Component;

import nhom4.public_service_management_system.service_required_document.dto.ServiceRequiredDocumentRequest;
import nhom4.public_service_management_system.service_required_document.dto.ServiceRequiredDocumentResponse;

@Component
public class ServiceRequiredDocumentMapper {
    public ServiceRequiredDocumentEntity toEntity(ServiceRequiredDocumentRequest request) {
        ServiceRequiredDocumentEntity document = new ServiceRequiredDocumentEntity();
        updateEntity(document, request);
        return document;
    }

    public ServiceRequiredDocumentResponse toResponse(ServiceRequiredDocumentEntity document) {
        return new ServiceRequiredDocumentResponse(
                document.getId(),
                document.getServiceId(),
                document.getDocumentName(),
                document.getRequired()
        );
    }

    public void updateEntity(ServiceRequiredDocumentEntity document, ServiceRequiredDocumentRequest request) {
        document.setServiceId(request.serviceId());
        document.setDocumentName(request.documentName());
        document.setRequired(request.required());
    }
}
