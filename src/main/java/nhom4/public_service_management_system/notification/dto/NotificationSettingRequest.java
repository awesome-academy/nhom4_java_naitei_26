package nhom4.public_service_management_system.notification.dto;

import jakarta.validation.constraints.NotNull;

public record NotificationSettingRequest(

        @NotNull(message = "Trạng thái thông báo không được để trống")
        Boolean enabled
) {
}