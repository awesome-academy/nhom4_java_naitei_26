package nhom4.public_service_management_system.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nhom4.public_service_management_system.enums.ApplicationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {

    private Long id;
    private String applicationCode;
    private Long citizenId;
    private Long serviceId;
    private Long assignedStaffId;
    private ApplicationStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime completedAt;
    private Map<String, Object> data;
    private String resultNote;
    private String rejectionReason;

    // Helper display fields
    private String citizenName;
    private String citizenIdentityNumber;
    private String citizenPhone;
    private String citizenEmail;

    private String serviceName;
    private String serviceCode;
    private String serviceCategory;
    private BigDecimal serviceFee;
    private String departmentName;

    private String assignedStaffName;
    private String assignedStaffPhone;

    public String getStatusBadgeClass() {
        if (status == null) return "bg-secondary";
        return switch (status) {
            case RECEIVED -> "bg-warning text-dark";
            case PROCESSING -> "bg-primary";
            case APPROVED -> "bg-success";
            case REJECTED -> "bg-danger";
        };
    }

    public String getStatusDisplayName() {
        if (status == null) return "Không xác định";
        return switch (status) {
            case RECEIVED -> "Chờ tiếp nhận";
            case PROCESSING -> "Đang xử lý";
            case APPROVED -> "Đã phê duyệt";
            case REJECTED -> "Bị từ chối";
        };
    }
}