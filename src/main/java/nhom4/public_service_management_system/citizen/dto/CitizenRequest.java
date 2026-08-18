package nhom4.public_service_management_system.citizen.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import nhom4.public_service_management_system.citizen.Gender;

import java.time.LocalDate;

// Request DTO 
public record CitizenRequest(

        Long userId,

        @NotBlank(message = "Họ tên không được để trống")
        @Size(max = 255, message = "Họ tên tối đa 255 ký tự")
        String name,

        @Past(message = "Ngày sinh phải là một ngày trong quá khứ")
        LocalDate dateOfBirth,

        Gender gender,

        @NotBlank(message = "Số CCCD/CMND không được để trống")
        @Pattern(regexp = "\\d{9}|\\d{12}", message = "Số CCCD/CMND phải gồm 9 hoặc 12 chữ số")
        String identityNumber,

        @Size(max = 1000, message = "Địa chỉ tối đa 1000 ký tự")
        String address,

        @Pattern(regexp = "^$|^[0-9+()\\-\\s]{8,20}$", message = "Số điện thoại không hợp lệ")
        String phone
) {
}
