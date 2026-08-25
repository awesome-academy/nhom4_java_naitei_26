package nhom4.public_service_management_system.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nhom4.public_service_management_system.department.DepartmentEntity;
import nhom4.public_service_management_system.department.DepartmentRepository;
import nhom4.public_service_management_system.exception.DuplicateResourceException;
import nhom4.public_service_management_system.exception.ResourceNotFoundException;
import nhom4.public_service_management_system.service.dto.ServiceRequest;
import nhom4.public_service_management_system.service.dto.ServiceResponse;
import nhom4.public_service_management_system.staff.StaffEntity;
import nhom4.public_service_management_system.staff.StaffRepository;
import nhom4.public_service_management_system.activity_log.ActivityLogService;

@Service
public class ServiceService {
    private final ServiceRepository serviceRepository;
    private final DepartmentRepository departmentRepository;
    private final StaffRepository staffRepository;
    private final ActivityLogService activityLogService;

    public ServiceService(ServiceRepository serviceRepository, DepartmentRepository departmentRepository, StaffRepository staffRepository, ActivityLogService activityLogService) {
        this.serviceRepository = serviceRepository;
        this.departmentRepository = departmentRepository;
        this.staffRepository = staffRepository;
        this.activityLogService = activityLogService;
    }

    @Transactional
    public ServiceResponse create(ServiceRequest request) {
        if (serviceRepository.existsByCode(request.code())) {
            throw new DuplicateResourceException(
                    "Mã dịch vụ '" + request.code() + "' đã tồn tại");
        }

        DepartmentEntity department = findDepartmentById(request.departmentId());
        StaffEntity assignedStaff = findStaffIfPresent(request.assignedStaffId());

        ServiceEntity entity = ServiceMapper.toEntity(request, department, assignedStaff);
        ServiceEntity saved = serviceRepository.save(entity);

        activityLogService.logCurrentAction("CREATE", "Tạo mới dịch vụ: " + request.name());

        return ServiceMapper.toResponse(saved);
    }

    @Transactional
    public ServiceResponse update(Long id, ServiceRequest request) {
        ServiceEntity entity = findEntityById(id);

        if(serviceRepository.existsByCodeAndIdNot(request.code(), id)) {
            throw new DuplicateResourceException(
                    "Mã dịch vụ '" + request.code() + "' đã dùng bởi dịch vụ khác");
        }

        DepartmentEntity department = findDepartmentById(request.departmentId());
        StaffEntity assignedStaff = findStaffIfPresent(request.assignedStaffId());

        ServiceMapper.updateEntityFromRequest(entity, request, department, assignedStaff);

        activityLogService.logCurrentAction("UPDATE", "Cập nhật dịch vụ ID: " + id);

        return ServiceMapper.toResponse(entity);
    }

    @Transactional
    public void delete(Long id) {
        ServiceEntity entity = findEntityById(id);
        serviceRepository.delete(entity);

        activityLogService.logCurrentAction("DELETE", "Xóa dịch vụ ID: " + id);
    }

    @Transactional(readOnly = true)
    public ServiceResponse getById(Long id) {
        return ServiceMapper.toResponse(findEntityById(id));
    }

    @Transactional(readOnly = true)
    public Page<ServiceResponse> getAll(Pageable pageable) {
        return serviceRepository.findAll(pageable)
                .map(ServiceMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ServiceResponse> searchByName(String name, Pageable pageable) {
        return serviceRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(ServiceMapper::toResponse);
    }

    private ServiceEntity findEntityById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy dịch vụ với id = " + id));
    }

    private DepartmentEntity findDepartmentById(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy phòng ban với id = " + departmentId));
    }

    private StaffEntity findStaffIfPresent(Long staffId) {
        if (staffId == null) {
            return null;
        }
        return staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy cán bộ với id = " + staffId));
    }
}