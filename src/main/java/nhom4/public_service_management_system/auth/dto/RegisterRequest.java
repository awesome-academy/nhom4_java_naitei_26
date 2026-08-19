package nhom4.public_service_management_system.auth.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import nhom4.public_service_management_system.enums.Gender;

public record RegisterRequest(

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không đúng định dạng")
        String email,

        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
        String password,

        @NotBlank(message = "Xác nhận mật khẩu không được để trống")
        String confirmPassword,

        @NotBlank(message = "Họ tên không được để trống")
        String name,

        @Past(message = "Ngày sinh phải là ngày trong quá khứ")
        LocalDate dateOfBirth,

        @NotNull(message = "Giới tính không được để trống")
        Gender gender,

        @NotBlank(message = "Số CCCD/CMND không được để trống")
        @Pattern(regexp = "^[0-9]{9,12}$", message = "Số CCCD/CMND phải từ 9-12 chữ số")
        String identityNumber,

        String address,

        @Pattern(regexp = "^(0[0-9]{9,10})?$", message = "Số điện thoại không đúng định dạng")
        String phone
) {
}
