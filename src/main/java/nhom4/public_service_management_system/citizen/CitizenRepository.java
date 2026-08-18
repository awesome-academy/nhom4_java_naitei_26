package nhom4.public_service_management_system.citizen;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CitizenRepository extends JpaRepository<CitizenEntity, Long> {

    Optional<CitizenEntity> findByUserId(Long userId);

    Optional<CitizenEntity> findByIdentityNumber(String identityNumber);

    boolean existsByIdentityNumber(String identityNumber);

    boolean existsByUserId(Long userId);

    Page<CitizenEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
