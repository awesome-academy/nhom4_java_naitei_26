package nhom4.public_service_management_system.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nhom4.public_service_management_system.auth.dto.RegisterRequest;
import nhom4.public_service_management_system.citizen.CitizenEntity;
import nhom4.public_service_management_system.citizen.CitizenRepository;
import nhom4.public_service_management_system.enums.UserRole;
import nhom4.public_service_management_system.enums.UserStatus;
import nhom4.public_service_management_system.exception.DuplicateResourceException;
import nhom4.public_service_management_system.user.UserEntity;
import nhom4.public_service_management_system.user.UserRepository;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final CitizenRepository citizenRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       CitizenRepository citizenRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.citizenRepository = citizenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest request) {
        // Kiểm tra email trùng
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email đã được sử dụng: " + request.email());
        }

        // Kiểm tra số CCCD trùng
        if (citizenRepository.existsByIdentityNumber(request.identityNumber())) {
            throw new DuplicateResourceException("Số CCCD/CMND đã được sử dụng: " + request.identityNumber());
        }

        // Kiểm tra mật khẩu xác nhận
        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp.");
        }

        // Tạo User
        UserEntity user = new UserEntity();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.ROLE_CITIZEN);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailNotificationEnabled(Boolean.TRUE);
        UserEntity savedUser = userRepository.save(user);

        // Tạo Citizen
        CitizenEntity citizen = new CitizenEntity();
        citizen.setUserId(savedUser.getId());
        citizen.setName(request.name());
        citizen.setDateOfBirth(request.dateOfBirth());
        citizen.setGender(request.gender());
        citizen.setIdentityNumber(request.identityNumber());
        citizen.setAddress(request.address());
        citizen.setPhone(request.phone());
        citizenRepository.save(citizen);
    }
}
