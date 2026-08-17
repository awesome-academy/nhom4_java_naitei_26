package nhom4.public_service_management_system.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import nhom4.public_service_management_system.user.dto.UserRequest;
import nhom4.public_service_management_system.user.dto.UserResponse;

public interface UserService {

    UserResponse create(UserRequest request);

    UserResponse update(Long id, UserRequest request);

    UserResponse findById(Long id);

    Page<UserResponse> findAll(Pageable pageable);

    void delete(Long id);
}
