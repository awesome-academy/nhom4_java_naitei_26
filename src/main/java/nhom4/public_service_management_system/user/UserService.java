package nhom4.public_service_management_system.user;

import nhom4.public_service_management_system.exception.DuplicateResourceException;
import nhom4.public_service_management_system.exception.ResourceNotFoundException;
import nhom4.public_service_management_system.user.dto.UserRequest;
import nhom4.public_service_management_system.user.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
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

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay user voi id: " + id));
        return userMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Khong tim thay user voi id: " + id);
        }
        userRepository.deleteById(id);
    }
}
