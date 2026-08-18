package nhom4.public_service_management_system.department;

import java.util.List;

import nhom4.public_service_management_system.department.dto.DepartmentRequest;
import nhom4.public_service_management_system.department.dto.DepartmentResponse;

public final class DepartmentMapper {

    private DepartmentMapper() {
    }

    public static DepartmentEntity toEntity(DepartmentRequest request) {
        if (request == null) {
            return null;
        }
        return new DepartmentEntity(
                request.name(),
                request.code(),
                request.address(),
                request.leaderStaffId()
        );
    }

    public static void updateEntityFromRequest(DepartmentEntity entity, DepartmentRequest request) {
        if (entity == null || request == null) {
            return;
        }
        entity.setName(request.name());
        entity.setCode(request.code());
        entity.setAddress(request.address());
        entity.setLeaderStaffId(request.leaderStaffId());
    }

    public static DepartmentResponse toResponse(DepartmentEntity entity) {
        if (entity == null) {
            return null;
        }
        return new DepartmentResponse(
                entity.getId(),
                entity.getName(),
                entity.getCode(),
                entity.getAddress(),
                entity.getLeaderStaffId()
        );
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
