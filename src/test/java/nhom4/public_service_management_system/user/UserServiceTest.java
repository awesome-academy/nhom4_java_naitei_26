package nhom4.public_service_management_system.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import nhom4.public_service_management_system.enums.UserRole;
import nhom4.public_service_management_system.enums.UserStatus;
import nhom4.public_service_management_system.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nhom4.public_service_management_system.exception.DuplicateResourceException;
import nhom4.public_service_management_system.user.dto.UserRequest;
import nhom4.public_service_management_system.user.dto.UserResponse;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserMapper userMapper;
    private UserService userService;

    private UserEntity userEntity;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        userService = new UserService(userRepository, userMapper);

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail("citizen@example.com");
        userEntity.setPassword("123456");
        userEntity.setRole(UserRole.CITIZEN);
        userEntity.setStatus(UserStatus.ACTIVE);
        userEntity.setEmailNotificationEnabled(true);

        userRequest = new UserRequest(
                "citizen@example.com",
                "123456",
                UserRole.CITIZEN,
                UserStatus.ACTIVE,
                true
        );
    }

    @Test
    void create_shouldReturnUserResponse_whenEmailNotExists() {
        when(userRepository.existsByEmail(userRequest.email())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserResponse response = userService.create(userRequest);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("citizen@example.com");
        assertThat(response.role()).isEqualTo(UserRole.CITIZEN);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void create_shouldThrowException_whenEmailAlreadyExists() {
        when(userRepository.existsByEmail(userRequest.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.create(userRequest))
                .isInstanceOf(DuplicateResourceException.class);

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void findById_shouldReturnUserResponse_whenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        UserResponse response = userService.findById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("citizen@example.com");
    }

    @Test
    void findById_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_shouldUpdateUser_whenUserExists() {
        UserRequest updateRequest = new UserRequest(
                "citizen@example.com",
                "newpassword",
                UserRole.CITIZEN,
                UserStatus.LOCKED,
                false
        );
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserResponse response = userService.update(1L, updateRequest);

        assertThat(response).isNotNull();
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void update_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99L, userRequest))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void delete_shouldRemoveUser_whenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_shouldThrowException_whenUserNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository, never()).deleteById(anyLong());
    }
}
