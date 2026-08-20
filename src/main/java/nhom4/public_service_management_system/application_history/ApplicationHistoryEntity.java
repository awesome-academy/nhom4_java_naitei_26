package nhom4.public_service_management_system.application_history;

import jakarta.persistence.*;
import nhom4.public_service_management_system.application.ApplicationEntity;
import nhom4.public_service_management_system.enums.ApplicationStatus;
import nhom4.public_service_management_system.user.UserEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "application_histories")
public class ApplicationHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private ApplicationEntity application;

    private ApplicationStatus oldStatus;
    private ApplicationStatus newStatus;

    @ManyToOne
    @JoinColumn(name = "changed_by")
    private UserEntity changedBy;
    private LocalDateTime changedAt;
    private String note;
}
