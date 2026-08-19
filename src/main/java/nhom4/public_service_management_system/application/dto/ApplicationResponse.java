package nhom4.public_service_management_system.application.dto;

import nhom4.public_service_management_system.enums.ApplicationStatus;
import java.time.LocalDateTime;
import java.util.Map;

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

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getApplicationCode() { return applicationCode; }
    public void setApplicationCode(String applicationCode) { this.applicationCode = applicationCode; }
    public Long getCitizenId() { return citizenId; }
    public void setCitizenId(Long citizenId) { this.citizenId = citizenId; }
    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }
    public Long getAssignedStaffId() { return assignedStaffId; }
    public void setAssignedStaffId(Long assignedStaffId) { this.assignedStaffId = assignedStaffId; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
    public String getResultNote() { return resultNote; }
    public void setResultNote(String resultNote) { this.resultNote = resultNote; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}