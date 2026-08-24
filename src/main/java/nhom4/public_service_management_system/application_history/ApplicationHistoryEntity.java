package nhom4.public_service_management_system.application_history;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nhom4.public_service_management_system.application.ApplicationEntity;
import nhom4.public_service_management_system.enums.ApplicationStatus;
import nhom4.public_service_management_system.user.UserEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "application_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private ApplicationEntity application;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus oldStatus;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus newStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private UserEntity changedBy;

    private LocalDateTime changedAt;

    // --- GETTERS & SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ApplicationEntity getApplication() { return application; }
    public void setApplication(ApplicationEntity application) { this.application = application; }

    public ApplicationStatus getOldStatus() { return oldStatus; }
    public void setOldStatus(ApplicationStatus oldStatus) { this.oldStatus = oldStatus; }

    public ApplicationStatus getNewStatus() { return newStatus; }
    public void setNewStatus(ApplicationStatus newStatus) { this.newStatus = newStatus; }

    public UserEntity getChangedBy() { return changedBy; }
    public void setChangedBy(UserEntity changedBy) { this.changedBy = changedBy; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
    @Column(columnDefinition = "TEXT")
    private String note;

    @PrePersist
    protected void onCreate() {
        if (this.changedAt == null) {
            this.changedAt = LocalDateTime.now();
        }
    }
}
