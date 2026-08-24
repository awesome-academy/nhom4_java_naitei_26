package nhom4.public_service_management_system.department.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DepartmentForm {

    @NotBlank(message = "Tên phòng ban không được để trống")
    @Size(max = 255, message = "Tên phòng ban tối đa 255 ký tự")
    private String name;

    @NotBlank(message = "Mã phòng ban không được để trống")
    @Size(max = 50, message = "Mã phòng ban tối đa 50 ký tự")
    private String code;

    @Size(max = 1000, message = "Địa chỉ tối đa 1000 ký tự")
    private String address;

    @NotNull(message = "Vui lòng chọn trưởng phòng ban")
    private Long leaderStaffId;


    public static DepartmentForm from(DepartmentResponse response) {
        DepartmentForm form = new DepartmentForm();
        form.setName(response.name());
        form.setCode(response.code());
        form.setAddress(response.address());
        form.setLeaderStaffId(response.leaderStaffId());
        return form;
    }

    public DepartmentRequest toRequest() {
        return new DepartmentRequest(name, code, address, leaderStaffId);
    }
}

