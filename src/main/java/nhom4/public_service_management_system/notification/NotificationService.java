package nhom4.public_service_management_system.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nhom4.public_service_management_system.application.ApplicationEntity;
import nhom4.public_service_management_system.application.ApplicationRepository;
import nhom4.public_service_management_system.exception.ResourceNotFoundException;
import nhom4.public_service_management_system.notification.dto.NotificationResponse;
import nhom4.public_service_management_system.user.UserEntity;
import nhom4.public_service_management_system.user.UserRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final JavaMailSender mailSender;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository,
            ApplicationRepository applicationRepository, JavaMailSender mailSender) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.mailSender = mailSender;
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable)
                .map(NotificationMapper::toResponse);
    }

    @Transactional
    public void toggleNotification(Long userId, boolean enabled) {
        UserEntity user = findUserById(userId);
        user.setEmailNotificationEnabled(enabled);
    }

    @Transactional
    public void createNotification(Long userId, Long applicationId, String message) {
        UserEntity user = findUserById(userId);

        if (!Boolean.TRUE.equals(user.getEmailNotificationEnabled())) {
            return;
        }

        ApplicationEntity application = applicationId != null ? findApplicationById(applicationId) : null;
        NotificationEntity entity = new NotificationEntity(user, application, message);
        notificationRepository.save(entity);

        // sendMail(user.getEmail(), message); // email disabled
    }

    private void sendMail(String toEmail, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        // mail.setFrom("phannthanh2005@gmail.com"); // Commented out to avoid SMTP auth issues
        mail.setTo(toEmail);
        mail.setSubject("Thông báo từ hệ thống dịch vụ công");
        mail.setText(message);
        try {
            mailSender.send(mail);
        } catch (org.springframework.mail.MailException ex) {
            // Log the error but do not propagate to avoid breaking the transaction
            System.err.println("Failed to send email notification: " + ex.getMessage());
        }
    }

    private UserEntity findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng với id = " + userId));
    }

    private ApplicationEntity findApplicationById(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy hồ sơ với id = " + applicationId));
    }
}