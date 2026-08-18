package nhom4.public_service_management_system.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import nhom4.public_service_management_system.user.UserRole;
import nhom4.public_service_management_system.user.UserStatus;

public record UserRequest(

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không đúng định dạng")
        String email,

        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
        String password,

        @NotNull(message = "Vai trò không được để trống")
        UserRole role,

        UserStatus status,

        Boolean emailNotificationEnabled
) {
}
