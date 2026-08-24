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

    @Column(columnDefinition = "TEXT")
    private String note;

    @PrePersist
    protected void onCreate() {
        if (this.changedAt == null) {
            this.changedAt = LocalDateTime.now();
        }
    }
}
