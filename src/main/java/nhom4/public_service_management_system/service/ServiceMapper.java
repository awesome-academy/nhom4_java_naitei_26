package nhom4.public_service_management_system.service;

import nhom4.public_service_management_system.department.DepartmentEntity;
import nhom4.public_service_management_system.service.dto.ServiceRequest;
import nhom4.public_service_management_system.service.dto.ServiceResponse;
import nhom4.public_service_management_system.staff.StaffEntity;

import java.util.Locale;

public class ServiceMapper {

    private ServiceMapper() {
    }

    public static ServiceEntity toEntity(ServiceRequest request, DepartmentEntity department, StaffEntity assignedStaff) {
        if (request == null) {
            return null;
        }
        return new ServiceEntity(
                request.name(),
                request.code(),
                request.description(),
                request.category(),
                request.processingTime(),
                request.fee(),
                department,
                assignedStaff
        );
    }

    public static void updateEntityFromRequest(ServiceEntity entity, ServiceRequest request, DepartmentEntity department, StaffEntity assignedStaff) {
        if (entity == null || request == null) {
            return;
        }
        entity.setName(request.name());
        entity.setCode(request.code());
        entity.setDescription(request.description());
        entity.setCategory(request.category());
        entity.setProcessingTime(request.processingTime());
        entity.setFee(request.fee());
        entity.setDepartment(department);
        entity.setAssignedStaff(assignedStaff);
    }

    public static ServiceResponse toResponse(ServiceEntity entity) {
        if (entity == null) {
            return null;
        }
        Long departmentId = entity.getDepartment() != null ? entity.getDepartment().getId() : null;
        String departmentName = entity.getDepartment() != null ? entity.getDepartment().getName() : null;
        Long staffId = entity.getAssignedStaff() != null ? entity.getAssignedStaff().getId() : null;
        String staffName = entity.getAssignedStaff() != null ? entity.getAssignedStaff().getName() : null;

        return new ServiceResponse(
                entity.getId(),
                entity.getName(),
                entity.getCode(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getProcessingTime(),
                entity.getFee(),
                departmentId,
                departmentName,
                staffId,
                staffName
        );
    }
}