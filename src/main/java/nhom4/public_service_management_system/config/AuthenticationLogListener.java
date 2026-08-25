package nhom4.public_service_management_system.config;

import nhom4.public_service_management_system.activity_log.ActivityLogService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationLogListener {

    private final ActivityLogService activityLogService;

    public AuthenticationLogListener(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @EventListener
    public void onLoginSuccess(AuthenticationSuccessEvent event) {
        UserDetails userDetails = (UserDetails) event.getAuthentication().getPrincipal();
        activityLogService.logCurrentAction("LOGIN", "Người dùng " + userDetails.getUsername() + " vừa đăng nhập vào hệ thống.");
    }

    @EventListener
    public void onLogoutSuccess(LogoutSuccessEvent event) {
        if (event.getAuthentication() != null && event.getAuthentication().getPrincipal() instanceof UserDetails userDetails) {
            activityLogService.logCurrentAction("LOGOUT", "Người dùng " + userDetails.getUsername() + " vừa đăng xuất khỏi hệ thống.");
        }
    }
}