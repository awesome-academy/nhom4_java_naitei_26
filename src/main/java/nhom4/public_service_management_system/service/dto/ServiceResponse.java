package nhom4.public_service_management_system.service.dto;

import java.math.BigDecimal;

public record ServiceResponse(
        Long id,
        String name,
        String code,
        String description,
        String category,
        Integer processingTime,
        BigDecimal fee,
        Long departmentId,
        String departmentName,
        Long assignedStaffId,
        String assignedStaffName
) {
}