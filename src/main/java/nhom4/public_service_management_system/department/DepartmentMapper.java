package nhom4.public_service_management_system.department;

import java.util.List;

import nhom4.public_service_management_system.department.dto.DepartmentRequest;
import nhom4.public_service_management_system.department.dto.DepartmentResponse;

import nhom4.public_service_management_system.staff.StaffEntity;

public final class DepartmentMapper {

    private DepartmentMapper() {
    }

    public static DepartmentEntity toEntity(DepartmentRequest request, StaffEntity leader) {
        if (request == null) {
            return null;
        }
        return new DepartmentEntity(
                request.name(),
                request.code(),
                request.address(),
                leader);
    }

    public static void updateEntityFromRequest(DepartmentEntity entity, DepartmentRequest request, StaffEntity leader) {
        if (entity == null || request == null) {
            return;
        }
        entity.setName(request.name());
        entity.setCode(request.code());
        entity.setAddress(request.address());
        entity.setLeaderStaffId(leader);
    }

    public static DepartmentResponse toResponse(DepartmentEntity entity) {
        if (entity == null) {
            return null;
        }
        Long leaderId = entity.getLeaderStaffId() != null ? entity.getLeaderStaffId().getId() : null;

        return new DepartmentResponse(
                entity.getId(),
                entity.getName(),
                entity.getCode(),
                entity.getAddress(),
                leaderId);
    }

    public static List<DepartmentResponse> toResponseList(List<DepartmentEntity> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(DepartmentMapper::toResponse)
                .toList();
    }

}
