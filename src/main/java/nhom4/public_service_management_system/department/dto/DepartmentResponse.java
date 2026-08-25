package nhom4.public_service_management_system.department.dto;

public record DepartmentResponse(
        Long id,
        String name,
        String code,
        String address,
        Long leaderStaffId,
        String leaderStaffName
) {
}
