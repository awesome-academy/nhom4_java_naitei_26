package nhom4.public_service_management_system.notification;

import jakarta.persistence.*;
import nhom4.public_service_management_system.application.ApplicationEntity;
import nhom4.public_service_management_system.user.UserEntity;

@Entity
@Table(name = "notifications")
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private ApplicationEntity application;

    private String message;
    private boolean isRead;
}
