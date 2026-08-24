package nhom4.public_service_management_system.application;

import nhom4.public_service_management_system.application.dto.ApplicationRequest;
import nhom4.public_service_management_system.application.dto.ApplicationResponse;
import nhom4.public_service_management_system.service.ServiceRepository;
import nhom4.public_service_management_system.staff.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMapper {

    @Autowired(required = false)
    private ServiceRepository serviceRepository;

    @Autowired(required = false)
    private StaffRepository staffRepository;

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

        if (entity.getCitizen() != null) {
            response.setCitizenName(entity.getCitizen().getName());
            response.setCitizenIdentityNumber(entity.getCitizen().getIdentityNumber());
            response.setCitizenPhone(entity.getCitizen().getPhone());
            if (entity.getCitizen().getUser() != null) {
                response.setCitizenEmail(entity.getCitizen().getUser().getEmail());
            }
        }

        if (serviceRepository != null && entity.getServiceId() != null) {
            serviceRepository.findById(entity.getServiceId()).ifPresent(service -> {
                response.setServiceName(service.getName());
                response.setServiceCode(service.getCode());
                response.setServiceCategory(service.getCategory());
                response.setServiceFee(service.getFee());
                if (service.getDepartment() != null) {
                    response.setDepartmentName(service.getDepartment().getName());
                }
            });
        }

        if (staffRepository != null && entity.getAssignedStaffId() != null) {
            staffRepository.findById(entity.getAssignedStaffId()).ifPresent(staff -> {
                response.setAssignedStaffName(staff.getName());
                response.setAssignedStaffPhone(staff.getPhone());
            });
        }

        return response;
    }
}