package nhom4.public_service_management_system.config;

import lombok.RequiredArgsConstructor;
import nhom4.public_service_management_system.enums.UserRole;
import nhom4.public_service_management_system.enums.UserStatus;
import nhom4.public_service_management_system.user.UserEntity;
import nhom4.public_service_management_system.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminConfig {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initAdmin() {
        return args -> {
            if (!userRepository.existsByEmail("admin@gmail.com")) {
                UserEntity admin = new UserEntity();
                admin.setEmail("admin@gmail.com");
                admin.setPassword(passwordEncoder.encode("123456"));
                admin.setRole(UserRole.ROLE_SUPER_ADMIN);
                admin.setStatus(UserStatus.ACTIVE);
                admin.setEmailNotificationEnabled(true);
                userRepository.save(admin);
            }
        };
    }

}
