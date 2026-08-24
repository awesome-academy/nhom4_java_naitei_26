package nhom4.public_service_management_system.service.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ServiceRequest(

        @NotBlank(message = "Tên dịch vụ không được để trống")
        @Size(max = 255, message = "Tên dịch vụ tối đa 255 ký tự")
        String name,

        @NotBlank(message = "Mã dịch vụ không được để trống")
        @Size(max = 50, message = "Mã dịch vụ tối đa 50 ký tự")
        String code,

        String description,

        @Size(max = 100, message = "Lĩnh vực tối đa 100 ký tự")
        String category,

        @Positive(message = "Thời hạn xử lý phải lớn hơn 0")
        Integer processingTime,

        @DecimalMin(value = "0.0", inclusive = true, message = "Lệ phí không được âm")
        BigDecimal fee,

        @NotNull(message = "Phòng ban phụ trách không được để trống")
        Long departmentId,

        Long assignedStaffId
) {
}