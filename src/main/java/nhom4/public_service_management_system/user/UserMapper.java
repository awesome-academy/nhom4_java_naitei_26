package nhom4.public_service_management_system.user;

import org.springframework.stereotype.Component;

import nhom4.public_service_management_system.user.dto.UserRequest;
import nhom4.public_service_management_system.user.dto.UserResponse;

@Component
public class UserMapper {

    public UserEntity toEntity(UserRequest request) {
        if (request == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setEmail(request.email());
        entity.setPassword(request.password());
        entity.setRole(request.role());
        entity.setStatus(request.status() != null ? request.status() : UserStatus.ACTIVE);
        entity.setEmailNotificationEnabled(
                request.emailNotificationEnabled() != null ? request.emailNotificationEnabled() : Boolean.TRUE
        );
        return entity;
    }

    public void updateEntity(UserEntity entity, UserRequest request) {
        entity.setEmail(request.email());
        if (request.password() != null && !request.password().isBlank()) {
            entity.setPassword(request.password());
        }
        entity.setRole(request.role());
        if (request.status() != null) {
            entity.setStatus(request.status());
        }
        if (request.emailNotificationEnabled() != null) {
            entity.setEmailNotificationEnabled(request.emailNotificationEnabled());
        }
    }

    public UserResponse toResponse(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return new UserResponse(
                entity.getId(),
                entity.getEmail(),
                entity.getRole(),
                entity.getStatus(),
                entity.getEmailNotificationEnabled()
        );
    }
}
