package nhom4.public_service_management_system.staff;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nhom4.public_service_management_system.exception.DuplicateResourceException;
import nhom4.public_service_management_system.exception.ResourceNotFoundException;
import nhom4.public_service_management_system.staff.dto.StaffRequest;
import nhom4.public_service_management_system.staff.dto.StaffResponse;

@Service
@Transactional
public class StaffService {
    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;

    public StaffService(StaffRepository staffRepository, StaffMapper staffMapper) {
        this.staffRepository = staffRepository;
        this.staffMapper = staffMapper;
    }

    public StaffResponse create(StaffRequest request) {
        if (staffRepository.existsByUserId(request.userId())) {
            throw new DuplicateResourceException("Staff already exists for user id: " + request.userId());
        }

        StaffEntity entity = staffMapper.toEntity(request);
        StaffEntity saved = staffRepository.save(entity);
        return staffMapper.toResponse(saved);
    }

    public StaffResponse update(Long id, StaffRequest request) {
        StaffEntity entity = findEntityById(id);

        if (staffRepository.existsByUserIdAndIdNot(request.userId(), id)) {
            throw new DuplicateResourceException("Staff already exists for user id: " + request.userId());
        }

        staffMapper.updateEntity(entity, request);
        StaffEntity saved = staffRepository.save(entity);
        return staffMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public StaffResponse getById(Long id) {
        return staffMapper.toResponse(findEntityById(id));
    }

    @Transactional(readOnly = true)
    public Page<StaffResponse> getAll(Pageable pageable) {
        return staffRepository.findAll(pageable).map(staffMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<StaffResponse> getAllByDepartmentId(Long departmentId, Pageable pageable) {
        return staffRepository.findByDepartmentId(departmentId, pageable).map(staffMapper::toResponse);
    }

    public StaffResponse assignDepartment(Long id, Long departmentId) {
        StaffEntity entity = findEntityById(id);
        entity.setDepartmentId(departmentId);
        StaffEntity saved = staffRepository.save(entity);
        return staffMapper.toResponse(saved);
    }

    public StaffResponse removeDepartment(Long id) {
        StaffEntity entity = findEntityById(id);
        entity.setDepartmentId(null);
        StaffEntity saved = staffRepository.save(entity);
        return staffMapper.toResponse(saved);
    }

    public void delete(Long id) {
        if (!staffRepository.existsById(id)) {
            throw new ResourceNotFoundException("Staff not found with id: " + id);
        }

        staffRepository.deleteById(id);
    }

    private StaffEntity findEntityById(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + id));
    }
}
