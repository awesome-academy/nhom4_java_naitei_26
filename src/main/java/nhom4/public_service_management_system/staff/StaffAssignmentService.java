package nhom4.public_service_management_system.staff;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nhom4.public_service_management_system.application.ApplicationEntity;
import nhom4.public_service_management_system.application.ApplicationRepository;
import nhom4.public_service_management_system.exception.ResourceNotFoundException;
import nhom4.public_service_management_system.service.ServiceEntity;
import nhom4.public_service_management_system.service.ServiceRepository;

@Service
@Transactional
public class StaffAssignmentService {
    private final StaffRepository staffRepository;
    private final ServiceRepository serviceRepository;
    private final ApplicationRepository applicationRepository;

    public StaffAssignmentService(
            StaffRepository staffRepository,
            ServiceRepository serviceRepository,
            ApplicationRepository applicationRepository) {
        this.staffRepository = staffRepository;
        this.serviceRepository = serviceRepository;
        this.applicationRepository = applicationRepository;
    }

    @Transactional(readOnly = true)
    public List<ServiceEntity> getAssignedServices(Long staffId) {
        return serviceRepository.findByAssignedStaffId(staffId);
    }

    @Transactional(readOnly = true)
    public List<ApplicationEntity> getAssignedApplications(Long staffId) {
        return applicationRepository.findByAssignedStaffId(staffId);
    }

    public void assignService(Long staffId, Long serviceId) {
        StaffEntity staff = findStaffById(staffId);
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));

        service.setAssignedStaff(staff);
        serviceRepository.save(service);
    }

    public void removeService(Long serviceId) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));

        service.setAssignedStaff(null);
        serviceRepository.save(service);
    }

    public void assignApplication(Long staffId, Long applicationId) {
        findStaffById(staffId);
        ApplicationEntity application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        application.setAssignedStaffId(staffId);
        applicationRepository.save(application);
    }

    public void removeApplication(Long applicationId) {
        ApplicationEntity application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        application.setAssignedStaffId(null);
        applicationRepository.save(application);
    }

    private StaffEntity findStaffById(Long staffId) {
        return staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + staffId));
    }
}
