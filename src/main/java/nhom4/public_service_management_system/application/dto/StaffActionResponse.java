package nhom4.public_service_management_system.application.dto;

import nhom4.public_service_management_system.enums.ApplicationStatus;

public record StaffActionResponse(
        String message,
        String applicationCode,
        ApplicationStatus newStatus
) {}