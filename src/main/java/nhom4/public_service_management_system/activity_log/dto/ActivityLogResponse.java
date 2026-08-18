package nhom4.public_service_management_system.activity_log.dto;

import java.time.LocalDateTime;

public class ActivityLogResponse {

    private Long id;
    private LocalDateTime createdAt;
    private String action;
    private Long userId;
    private String userEmail;
    private String description;

    public ActivityLogResponse() {
    }

    public ActivityLogResponse(Long id, LocalDateTime createdAt, String action, Long userId, String userEmail, String description) {
        this.id = id;
        this.createdAt = createdAt;
        this.action = action;
        this.userId = userId;
        this.userEmail = userEmail;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}