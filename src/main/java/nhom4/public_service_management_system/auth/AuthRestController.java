package nhom4.public_service_management_system.auth;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import nhom4.public_service_management_system.auth.dto.LoginRequest;
import nhom4.public_service_management_system.auth.dto.RegisterRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@RestController
@RequestMapping("/api")
public class AuthRestController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public AuthRestController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Đăng ký thành công",
                    "status", HttpStatus.CREATED.value()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage(),
                    "status", HttpStatus.BAD_REQUEST.value()
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(request.email(), request.password());
            Authentication authenticationResponse = this.authenticationManager.authenticate(authenticationRequest);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authenticationResponse);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, httpRequest, httpResponse);

            return ResponseEntity.ok(Map.of(
                    "message", "Đăng nhập thành công",
                    "status", HttpStatus.OK.value()
            ));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "message", "Email hoặc mật khẩu không chính xác",
                    "status", HttpStatus.UNAUTHORIZED.value()
            ));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of(
                "message", "Đăng xuất thành công",
                "status", HttpStatus.OK.value()
        ));
    }
}
