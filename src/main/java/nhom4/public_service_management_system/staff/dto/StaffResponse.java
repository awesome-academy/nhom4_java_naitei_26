package nhom4.public_service_management_system.staff.dto;

public record StaffResponse(
        Long id,
        Long userId,
        String name,
        String phone,
        String address,
        Long departmentId
) {
}
