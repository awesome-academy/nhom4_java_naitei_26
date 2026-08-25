package nhom4.public_service_management_system.department;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nhom4.public_service_management_system.exception.DuplicateResourceException;
import nhom4.public_service_management_system.exception.ResourceNotFoundException;
import nhom4.public_service_management_system.department.dto.DepartmentRequest;
import nhom4.public_service_management_system.department.dto.DepartmentResponse;

import nhom4.public_service_management_system.staff.StaffRepository;
import nhom4.public_service_management_system.staff.StaffEntity;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final StaffRepository staffRepository;

    public DepartmentService(DepartmentRepository departmentRepository, StaffRepository staffRepository) {
        this.departmentRepository = departmentRepository;
        this.staffRepository = staffRepository;
    }

    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        if (departmentRepository.existsByCode(request.code())) {
            throw new DuplicateResourceException(
                    "Mã phòng ban '" + request.code() + "' đã tồn tại");
        }
        StaffEntity leader = staffRepository.findById(request.leaderStaffId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Không tìm thấy staff id = " + request.leaderStaffId()));

        DepartmentEntity entity = DepartmentMapper.toEntity(request, leader);
        DepartmentEntity saved = departmentRepository.save(entity);
        leader.setDepartmentId(saved.getId());
        return DepartmentMapper.toResponse(saved);
    }

    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        DepartmentEntity entity = findEntityById(id);

        if (departmentRepository.existsByCodeAndIdNot(request.code(), id)) {
            throw new DuplicateResourceException(
                    "Mã phòng ban '" + request.code() + "' đã được dùng bởi phòng ban khác");
        }
        StaffEntity leader = staffRepository.findById(request.leaderStaffId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Không tìm thấy staff id = " + request.leaderStaffId()));

        DepartmentMapper.updateEntityFromRequest(entity, request, leader);
        return DepartmentMapper.toResponse(entity);
    }

    @Transactional
    public void delete(Long id) {
        DepartmentEntity entity = findEntityById(id);
        departmentRepository.delete(entity);
    }

    @Transactional(readOnly = true)
    public DepartmentResponse getById(Long id) {
        return DepartmentMapper.toResponse(findEntityById(id));
    }

    @Transactional(readOnly = true)
    public Page<DepartmentResponse> getAll(Pageable pageable) {
        return departmentRepository.findAll(pageable)
                .map(DepartmentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<DepartmentResponse> searchByName(String name, Pageable pageable) {
        return departmentRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(DepartmentMapper::toResponse);
    }

    private DepartmentEntity findEntityById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy phòng ban với id = " + id));
    }
}
