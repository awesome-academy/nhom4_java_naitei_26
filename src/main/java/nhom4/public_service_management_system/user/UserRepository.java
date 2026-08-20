package nhom4.public_service_management_system.user;

import java.util.Collection;
import java.util.Optional;

import nhom4.public_service_management_system.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<UserEntity> findByRole(UserRole role, Pageable pageable);

    Page<UserEntity> findByRoleIn(Collection<UserRole> roles, Pageable pageable);

    long countByIdGreaterThan(Long id);

    long countByIdGreaterThanAndRoleIn(Long id, Collection<UserRole> roles);
}
