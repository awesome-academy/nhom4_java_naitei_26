package nhom4.public_service_management_system.user;

import java.util.List;

import nhom4.public_service_management_system.citizen.CitizenEntity;
import nhom4.public_service_management_system.citizen.CitizenRepository;
import nhom4.public_service_management_system.enums.UserRole;
import nhom4.public_service_management_system.enums.UserStatus;
import nhom4.public_service_management_system.exception.DuplicateResourceException;
import nhom4.public_service_management_system.exception.ResourceNotFoundException;
import nhom4.public_service_management_system.staff.StaffEntity;
import nhom4.public_service_management_system.staff.StaffRepository;
import nhom4.public_service_management_system.user.dto.UserForm;
import nhom4.public_service_management_system.user.dto.UserProfileResponse;
import nhom4.public_service_management_system.user.dto.UserRequest;
import nhom4.public_service_management_system.user.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class UserService {

    private static final List<UserRole> MANAGED_ROLES = List.of(UserRole.CITIZEN, UserRole.STAFF);

    private final UserRepository userRepository;
    private final CitizenRepository citizenRepository;
    private final StaffRepository staffRepository;
    private final UserMapper userMapper;

    public UserService(
            UserRepository userRepository,
            CitizenRepository citizenRepository,
            StaffRepository staffRepository,
            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.citizenRepository = citizenRepository;
        this.staffRepository = staffRepository;
        this.userMapper = userMapper;
    }

    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email da duoc su dung: " + request.email());
        }
        UserEntity entity = userMapper.toEntity(request);
        UserEntity saved = userRepository.save(entity);
        return userMapper.toResponse(saved);
    }

    public UserResponse update(Long id, UserRequest request) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay user voi id: " + id));

        if (!entity.getEmail().equalsIgnoreCase(request.email())
                && userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email da duoc su dung: " + request.email());
        }

        userMapper.updateEntity(entity, request);
        UserEntity saved = userRepository.save(entity);
        return userMapper.toResponse(saved);
    }

    public UserProfileResponse createWithProfile(UserForm form) {
        validateManagedRole(form.getRole());
        validateUniqueEmail(form.getEmail(), null);
        validateUniquePhone(form.getPhone(), null);

        UserEntity entity = userMapper.toEntity(form.toRequest());
        UserEntity saved = userRepository.save(entity);
        saveProfile(saved.getId(), form);
        return toProfileResponse(saved, getDisplayId(saved.getId()));
    }

    public UserProfileResponse updateWithProfileByDisplayId(Long displayId, UserForm form) {
        return updateWithProfile(resolveUserIdByDisplayId(displayId), form);
    }

    public UserProfileResponse updateWithProfile(Long id, UserForm form) {
        validateManagedRole(form.getRole());
        UserEntity entity = findEntityOrThrow(id);

        validateUniqueEmail(form.getEmail(), id);
        validateUniquePhone(form.getPhone(), id);

        UserRole oldRole = entity.getRole();
        userMapper.updateEntity(entity, form.toRequest());
        UserEntity saved = userRepository.save(entity);

        if (oldRole != form.getRole()) {
            deleteProfile(id);
            saveProfile(id, form);
        } else {
            updateProfile(id, form);
        }

        return toProfileResponse(saved, getDisplayId(id));
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        UserEntity entity = findEntityOrThrow(id);
        return userMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public long getDisplayId(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Khong tim thay user voi id: " + id);
        }
        return userRepository.countByIdGreaterThanAndRoleIn(id, MANAGED_ROLES) + 1;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse findProfileById(Long id) {
        UserEntity entity = findEntityOrThrow(id);
        validateManagedRole(entity.getRole());
        return toProfileResponse(entity, getDisplayId(id));
    }

    @Transactional(readOnly = true)
    public UserProfileResponse findProfileByDisplayId(Long displayId) {
        return findProfileById(resolveUserIdByDisplayId(displayId));
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(UserRole role, Pageable pageable) {
        if (role == null) {
            return findAll(pageable);
        }
        return userRepository.findByRole(role, pageable).map(userMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<UserProfileResponse> findProfiles(UserRole role, Pageable pageable) {
        Page<UserEntity> users;
        if (role == null) {
            users = userRepository.findByRoleIn(MANAGED_ROLES, pageable);
        } else {
            validateManagedRole(role);
            users = userRepository.findByRole(role, pageable);
        }

        List<UserProfileResponse> content = users.getContent().stream()
                .map(entity -> toProfileResponse(entity, getDisplayId(entity.getId())))
                .toList();
        return new PageImpl<>(content, pageable, users.getTotalElements());
    }

    public UserResponse lock(Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay user voi id: " + id));
        entity.setStatus(UserStatus.LOCKED);
        UserEntity saved = userRepository.save(entity);
        return userMapper.toResponse(saved);
    }

    public UserResponse lockByDisplayId(Long displayId) {
        return lock(resolveUserIdByDisplayId(displayId));
    }

    public void deleteByDisplayId(Long displayId) {
        delete(resolveUserIdByDisplayId(displayId));
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Khong tim thay user voi id: " + id);
        }
        deleteProfile(id);
        userRepository.deleteById(id);
    }

    private UserEntity findEntityOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay user voi id: " + id));
    }

    private Long resolveUserIdByDisplayId(Long displayId) {
        if (displayId == null || displayId < 1 || displayId > Integer.MAX_VALUE) {
            throw new ResourceNotFoundException("Khong tim thay user voi id hien thi: " + displayId);
        }

        Pageable pageable = PageRequest.of(
                Math.toIntExact(displayId - 1),
                1,
                Sort.by("id").descending()
        );
        Page<UserEntity> users = userRepository.findByRoleIn(MANAGED_ROLES, pageable);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("Khong tim thay user voi id hien thi: " + displayId);
        }
        return users.getContent().get(0).getId();
    }

    private void validateManagedRole(UserRole role) {
        if (!MANAGED_ROLES.contains(role)) {
            throw new IllegalArgumentException("Chi ho tro vai tro CITIZEN hoac STAFF");
        }
    }

    private void validateUniqueEmail(String email, Long excludingUserId) {
        userRepository.findByEmail(email).ifPresent(existing -> {
            if (excludingUserId == null || !existing.getId().equals(excludingUserId)) {
                throw new DuplicateResourceException("Email da duoc su dung: " + email);
            }
        });
    }

    private void validateUniquePhone(String phone, Long excludingUserId) {
        if (!StringUtils.hasText(phone)) {
            return;
        }

        citizenRepository.findByPhone(phone).ifPresent(existing -> {
            if (excludingUserId == null || !existing.getUserId().equals(excludingUserId)) {
                throw new DuplicateResourceException("So dien thoai da duoc su dung: " + phone);
            }
        });

        staffRepository.findByPhone(phone).ifPresent(existing -> {
            if (excludingUserId == null || !existing.getUserId().equals(excludingUserId)) {
                throw new DuplicateResourceException("So dien thoai da duoc su dung: " + phone);
            }
        });
    }

    private void saveProfile(Long userId, UserForm form) {
        if (form.getRole() == UserRole.CITIZEN) {
            CitizenEntity citizen = new CitizenEntity();
            citizen.setUserId(userId);
            citizen.setName(form.getName());
            citizen.setPhone(form.getPhone());
            citizen.setAddress(form.getAddress());
            citizenRepository.save(citizen);
            return;
        }

        StaffEntity staff = new StaffEntity();
        staff.setUserId(userId);
        staff.setName(form.getName());
        staff.setPhone(form.getPhone());
        staff.setAddress(form.getAddress());
        staffRepository.save(staff);
    }

    private void updateProfile(Long userId, UserForm form) {
        if (form.getRole() == UserRole.CITIZEN) {
            CitizenEntity citizen = citizenRepository.findByUserId(userId).orElseGet(CitizenEntity::new);
            citizen.setUserId(userId);
            citizen.setName(form.getName());
            citizen.setPhone(form.getPhone());
            citizen.setAddress(form.getAddress());
            citizenRepository.save(citizen);
            return;
        }

        StaffEntity staff = staffRepository.findByUserId(userId).orElseGet(StaffEntity::new);
        staff.setUserId(userId);
        staff.setName(form.getName());
        staff.setPhone(form.getPhone());
        staff.setAddress(form.getAddress());
        staffRepository.save(staff);
    }

    private void deleteProfile(Long userId) {
        citizenRepository.deleteByUserId(userId);
        staffRepository.deleteByUserId(userId);
    }

    private UserProfileResponse toProfileResponse(UserEntity user, long displayId) {
        ProfileData profile = findProfile(user);
        return new UserProfileResponse(
                user.getId(),
                displayId,
                profile.name(),
                user.getEmail(),
                user.getRole(),
                profile.phone(),
                profile.address(),
                user.getStatus(),
                user.getEmailNotificationEnabled()
        );
    }

    private ProfileData findProfile(UserEntity user) {
        if (user.getRole() == UserRole.CITIZEN) {
            return citizenRepository.findByUserId(user.getId())
                    .map(citizen -> new ProfileData(citizen.getName(), citizen.getPhone(), citizen.getAddress()))
                    .orElse(ProfileData.empty());
        }

        if (user.getRole() == UserRole.STAFF) {
            return staffRepository.findByUserId(user.getId())
                    .map(staff -> new ProfileData(staff.getName(), staff.getPhone(), staff.getAddress()))
                    .orElse(ProfileData.empty());
        }

        return ProfileData.empty();
    }

    private record ProfileData(String name, String phone, String address) {
        private static ProfileData empty() {
            return new ProfileData("", "", "");
        }
    }
}
