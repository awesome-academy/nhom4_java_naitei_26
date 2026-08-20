package nhom4.public_service_management_system.citizen.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import nhom4.public_service_management_system.enums.Gender;

import java.time.LocalDate;

public record CitizenUpdateRequest(

        Long userId,

        @NotBlank(message = "Ho ten khong duoc de trong")
        @Size(max = 255, message = "Ho ten toi da 255 ky tu")
        String name,

        @Past(message = "Ngay sinh phai la mot ngay trong qua khu")
        LocalDate dateOfBirth,

        Gender gender,

        @Size(max = 1000, message = "Dia chi toi da 1000 ky tu")
        String address,

        @Pattern(regexp = "^$|^[0-9+()\\-\\s]{8,20}$", message = "So dien thoai khong hop le")
        String phone
) {
}
