package nhom4.public_service_management_system.department.dto;

import nhom4.public_service_management_system.staff.StaffEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartmentRequest(

        @NotBlank(message = "Tên phòng ban không được để trống")
        @Size(max = 255, message = "Tên phòng ban tối đa 255 ký tự")

        String name,

        @NotBlank(message = "Mã phòng ban không được để trống")
        @Size(max = 50, message = "Mã phòng ban tối đa 50 ký tự")

        String code,

        @Size(max = 1000, message = "Địa chỉ tối đa 1000 ký tự")

        String address,

        StaffEntity leaderStaffId
        ) {
}
