package nhom4.public_service_management_system.activity_log.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ActivityLogRequest {

    @NotBlank(message = "Action cannot be blank")
    @Size(max = 100, message = "Action must not exceed 100 characters")
    private String action;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    private String description;

    public ActivityLogRequest() {
    }

    public ActivityLogRequest(String action, Long userId, String description) {
        this.action = action;
        this.userId = userId;
        this.description = description;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}