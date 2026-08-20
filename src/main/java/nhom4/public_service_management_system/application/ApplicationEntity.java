package nhom4.public_service_management_system.application;

import jakarta.persistence.*;
import nhom4.public_service_management_system.application_document.ApplicationDocumentEntity;
import nhom4.public_service_management_system.application_history.ApplicationHistoryEntity;
import nhom4.public_service_management_system.enums.ApplicationStatus;
import nhom4.public_service_management_system.notification.NotificationEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.List;


@Entity
@Table(name = "applications")
public class ApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_code", unique = true, length = 50, nullable = false)
    private String applicationCode;

    @Column(name = "citizen_id", nullable = false)
    private Long citizenId;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @Column(name = "assigned_staff_id")
    private Long assignedStaffId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Sử dụng tính năng của Hibernate 6 để map cột JSON trong DB với Map của Java
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", columnDefinition = "json")
    private Map<String, Object> data;

    @Column(name = "result_note", columnDefinition = "TEXT")
    private String resultNote;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
        this.status = ApplicationStatus.RECEIVED;
        // Tự động sinh mã hồ sơ (Ví dụ: APP-UUID)
        if (this.applicationCode == null) {
            this.applicationCode = "APP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    @OneToMany(mappedBy = "notificationEntity", cascade = CascadeType.ALL)
    private List<NotificationEntity> notificationEntities = new ArrayList<>();

    @OneToMany(mappedBy = "applicationHistory", cascade = CascadeType.ALL)
    private List<ApplicationHistoryEntity> applicationHistoryEntities = new ArrayList<>();

    @OneToMany(mappedBy = "applicationDocument", cascade = CascadeType.ALL)
    private List<ApplicationDocumentEntity> applicationDocumentEntities = new ArrayList<>();

    // Getters and Setters

    public List<NotificationEntity> getNotificationEntities() {
        return notificationEntities;
    }

    public void setNotificationEntities(List<NotificationEntity> notificationEntities) {
        this.notificationEntities = notificationEntities;
    }

    public List<ApplicationHistoryEntity> getApplicationHistoryEntities() {
        return applicationHistoryEntities;
    }

    public void setApplicationHistoryEntities(List<ApplicationHistoryEntity> applicationHistoryEntities) {
        this.applicationHistoryEntities = applicationHistoryEntities;
    }

    public List<ApplicationDocumentEntity> getApplicationDocumentEntities() {
        return applicationDocumentEntities;
    }

    public void setApplicationDocumentEntities(List<ApplicationDocumentEntity> applicationDocumentEntities) {
        this.applicationDocumentEntities = applicationDocumentEntities;
    }

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