package nhom4.public_service_management_system.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}
