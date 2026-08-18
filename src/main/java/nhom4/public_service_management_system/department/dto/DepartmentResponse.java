package nhom4.public_service_management_system.department.dto;

import nhom4.public_service_management_system.staff.StaffEntity;

public record DepartmentResponse(
        Long id,
        String name,
        String code,
        String address,
        StaffEntity leaderStaffId
) {
}
