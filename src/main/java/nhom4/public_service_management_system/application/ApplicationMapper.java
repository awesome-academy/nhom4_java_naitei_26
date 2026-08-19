package nhom4.public_service_management_system.application;

import nhom4.public_service_management_system.application.dto.ApplicationRequest;
import nhom4.public_service_management_system.application.dto.ApplicationResponse;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMapper {

    public ApplicationEntity toEntity(ApplicationRequest request) {
        if (request == null) return null;

        ApplicationEntity entity = new ApplicationEntity();
        entity.setCitizenId(request.getCitizenId());
        entity.setServiceId(request.getServiceId());
        entity.setData(request.getData());

        return entity;
    }

    public ApplicationResponse toResponse(ApplicationEntity entity) {
        if (entity == null) return null;

        ApplicationResponse response = new ApplicationResponse();
        response.setId(entity.getId());
        response.setApplicationCode(entity.getApplicationCode());
        response.setCitizenId(entity.getCitizenId());
        response.setServiceId(entity.getServiceId());
        response.setAssignedStaffId(entity.getAssignedStaffId());
        response.setStatus(entity.getStatus());
        response.setSubmittedAt(entity.getSubmittedAt());
        response.setCompletedAt(entity.getCompletedAt());
        response.setData(entity.getData());
        response.setResultNote(entity.getResultNote());
        response.setRejectionReason(entity.getRejectionReason());

        return response;
    }
}