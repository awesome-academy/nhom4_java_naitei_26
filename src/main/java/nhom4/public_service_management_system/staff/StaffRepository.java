package nhom4.public_service_management_system.staff;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JpaRepository<StaffEntity, Long> {
    Optional<StaffEntity> findByUserId(Long userId);

    Optional<StaffEntity> findByPhone(String phone);

    Page<StaffEntity> findByDepartmentId(Long departmentId, Pageable pageable);

    List<StaffEntity> findByDepartmentId(Long departmentId);

    List<StaffEntity> findByDepartmentIdIsNull();

    boolean existsByUserId(Long userId);

    boolean existsByUserIdAndIdNot(Long userId, Long id);

    void deleteByUserId(Long userId);
}
