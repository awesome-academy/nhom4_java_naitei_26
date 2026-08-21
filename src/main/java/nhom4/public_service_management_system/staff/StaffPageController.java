package nhom4.public_service_management_system.staff;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import nhom4.public_service_management_system.application.ApplicationRepository;
import nhom4.public_service_management_system.department.DepartmentRepository;
import nhom4.public_service_management_system.service.ServiceRepository;
import nhom4.public_service_management_system.staff.dto.StaffResponse;

@Controller
@RequestMapping("/admin/staff")
public class StaffPageController {
    private final StaffService staffService;
    private final StaffAssignmentService staffAssignmentService;
    private final DepartmentRepository departmentRepository;
    private final ServiceRepository serviceRepository;
    private final ApplicationRepository applicationRepository;

    public StaffPageController(
            StaffService staffService,
            StaffAssignmentService staffAssignmentService,
            DepartmentRepository departmentRepository,
            ServiceRepository serviceRepository,
            ApplicationRepository applicationRepository) {
        this.staffService = staffService;
        this.staffAssignmentService = staffAssignmentService;
        this.departmentRepository = departmentRepository;
        this.serviceRepository = serviceRepository;
        this.applicationRepository = applicationRepository;
    }

    @GetMapping
    public String index(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by("id").descending());
        Page<StaffResponse> staff = departmentId == null
                ? staffService.getAll(pageable)
                : staffService.getAllByDepartmentId(departmentId, pageable);

        model.addAttribute("staff", staff);
        model.addAttribute("departments", departmentRepository.findAll(Sort.by("name").ascending()));
        model.addAttribute("selectedDepartmentId", departmentId);
        return "staff/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("staff", staffService.getById(id));
        model.addAttribute("departments", departmentRepository.findAll(Sort.by("name").ascending()));
        model.addAttribute("services", serviceRepository.findAll(Sort.by("name").ascending()));
        model.addAttribute("applications", applicationRepository.findAll(Sort.by("id").descending()));
        model.addAttribute("assignedServices", staffAssignmentService.getAssignedServices(id));
        model.addAttribute("assignedApplications", staffAssignmentService.getAssignedApplications(id));
        return "staff/detail";
    }

    @PostMapping("/{id}/department")
    public String assignDepartment(
            @PathVariable Long id,
            @RequestParam Long departmentId,
            RedirectAttributes redirectAttributes) {
        staffService.assignDepartment(id, departmentId);
        redirectAttributes.addFlashAttribute("successMessage", "Da gan can bo vao phong ban.");
        return "redirect:/admin/staff/" + id;
    }

    @PostMapping("/{id}/department/remove")
    public String removeDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        staffService.removeDepartment(id);
        redirectAttributes.addFlashAttribute("successMessage", "Da xoa can bo khoi phong ban.");
        return "redirect:/admin/staff/" + id;
    }

    @PostMapping("/{id}/services")
    public String assignService(
            @PathVariable Long id,
            @RequestParam Long serviceId,
            RedirectAttributes redirectAttributes) {
        staffAssignmentService.assignService(id, serviceId);
        redirectAttributes.addFlashAttribute("successMessage", "Da gan dich vu cho can bo.");
        return "redirect:/admin/staff/" + id;
    }

    @PostMapping("/{id}/services/{serviceId}/remove")
    public String removeService(
            @PathVariable Long id,
            @PathVariable Long serviceId,
            RedirectAttributes redirectAttributes) {
        staffAssignmentService.removeService(serviceId);
        redirectAttributes.addFlashAttribute("successMessage", "Da xoa dich vu khoi can bo.");
        return "redirect:/admin/staff/" + id;
    }

    @PostMapping("/{id}/applications")
    public String assignApplication(
            @PathVariable Long id,
            @RequestParam Long applicationId,
            RedirectAttributes redirectAttributes) {
        staffAssignmentService.assignApplication(id, applicationId);
        redirectAttributes.addFlashAttribute("successMessage", "Da gan ho so cho can bo.");
        return "redirect:/admin/staff/" + id;
    }

    @PostMapping("/{id}/applications/{applicationId}/remove")
    public String removeApplication(
            @PathVariable Long id,
            @PathVariable Long applicationId,
            RedirectAttributes redirectAttributes) {
        staffAssignmentService.removeApplication(applicationId);
        redirectAttributes.addFlashAttribute("successMessage", "Da xoa ho so khoi can bo.");
        return "redirect:/admin/staff/" + id;
    }
}
