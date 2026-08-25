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
import nhom4.public_service_management_system.activity_log.ActivityLogService;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final StaffRepository staffRepository;
    private final ActivityLogService activityLogService;

    public DepartmentService(DepartmentRepository departmentRepository, StaffRepository staffRepository, ActivityLogService activityLogService) {
        this.departmentRepository = departmentRepository;
        this.staffRepository = staffRepository;
        this.activityLogService = activityLogService;
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

        // Ghi Log
        activityLogService.logCurrentAction("CREATE", "Tạo mới phòng ban: " + request.name());

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

        // Ghi Log
        activityLogService.logCurrentAction("UPDATE", "Cập nhật phòng ban ID: " + id);

        return DepartmentMapper.toResponse(entity);
    }

    @Transactional
    public void delete(Long id) {
        DepartmentEntity entity = findEntityById(id);
        departmentRepository.delete(entity);

        // Ghi Log
        activityLogService.logCurrentAction("DELETE", "Xóa phòng ban ID: " + id);
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