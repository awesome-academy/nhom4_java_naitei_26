package nhom4.public_service_management_system.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import nhom4.public_service_management_system.citizen.CitizenEntity;
import nhom4.public_service_management_system.citizen.CitizenRepository;
import nhom4.public_service_management_system.enums.UserRole;
import nhom4.public_service_management_system.enums.UserStatus;
import nhom4.public_service_management_system.exception.ResourceNotFoundException;
import nhom4.public_service_management_system.staff.StaffRepository;
import nhom4.public_service_management_system.user.dto.UserForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

    @Mock
    private CitizenRepository citizenRepository;

    @Mock
    private StaffRepository staffRepository;

    private UserMapper userMapper;
    private UserService userService;

    private UserEntity userEntity;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        userService = new UserService(userRepository, citizenRepository, staffRepository, userMapper);

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
    void createWithProfile_shouldThrowException_whenEmailAlreadyExists() {
        UserForm form = createProfileForm();
        when(userRepository.findByEmail(form.getEmail())).thenReturn(Optional.of(userEntity));

        assertThatThrownBy(() -> userService.createWithProfile(form))
                .isInstanceOf(DuplicateResourceException.class);

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void createWithProfile_shouldThrowException_whenPhoneAlreadyExists() {
        UserForm form = createProfileForm();
        CitizenEntity existingCitizen = new CitizenEntity();
        existingCitizen.setUserId(2L);
        existingCitizen.setPhone(form.getPhone());
        when(userRepository.findByEmail(form.getEmail())).thenReturn(Optional.empty());
        when(citizenRepository.findByPhone(form.getPhone())).thenReturn(Optional.of(existingCitizen));

        assertThatThrownBy(() -> userService.createWithProfile(form))
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
    void getDisplayId_shouldReturnSequentialNumber_whenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.countByIdGreaterThanAndRoleIn(1L, List.of(UserRole.CITIZEN, UserRole.STAFF))).thenReturn(2L);

        long displayId = userService.getDisplayId(1L);

        assertThat(displayId).isEqualTo(3L);
    }

    @Test
    void findAll_shouldFilterByRole_whenRoleProvided() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(userRepository.findByRole(UserRole.CITIZEN, pageable))
                .thenReturn(new PageImpl<>(List.of(userEntity), pageable, 1));

        Page<UserResponse> response = userService.findAll(UserRole.CITIZEN, pageable);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).role()).isEqualTo(UserRole.CITIZEN);
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

    @Test
    void lock_shouldSetStatusLocked_whenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserResponse response = userService.lock(1L);

        assertThat(response.status()).isEqualTo(UserStatus.LOCKED);
        assertThat(userEntity.getStatus()).isEqualTo(UserStatus.LOCKED);
        verify(userRepository).save(userEntity);
    }

    private UserForm createProfileForm() {
        UserForm form = new UserForm();
        form.setName("Nguyen Van A");
        form.setEmail("citizen@example.com");
        form.setPassword("123456");
        form.setRole(UserRole.CITIZEN);
        form.setStatus(UserStatus.ACTIVE);
        form.setEmailNotificationEnabled(true);
        form.setPhone("0901234567");
        form.setAddress("Ha Noi");
        return form;
    }
}
