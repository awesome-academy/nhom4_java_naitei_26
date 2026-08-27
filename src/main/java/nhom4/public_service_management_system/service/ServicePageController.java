package nhom4.public_service_management_system.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import nhom4.public_service_management_system.department.DepartmentRepository;
import nhom4.public_service_management_system.exception.DuplicateResourceException;
import nhom4.public_service_management_system.exception.ResourceNotFoundException;
import nhom4.public_service_management_system.exception.ServiceInUseException;
import nhom4.public_service_management_system.service.dto.ServiceRequest;
import nhom4.public_service_management_system.service.dto.ServiceResponse;
import nhom4.public_service_management_system.staff.StaffRepository;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/admin/services")
public class ServicePageController {

    private final ServiceService serviceService;
    private final DepartmentRepository departmentRepository;
    private final StaffRepository staffRepository;

    public ServicePageController(ServiceService serviceService,
                                 DepartmentRepository departmentRepository,
                                 StaffRepository staffRepository) {
        this.serviceService = serviceService;
        this.departmentRepository = departmentRepository;
        this.staffRepository = staffRepository;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String name,
                       @org.springframework.data.web.PageableDefault(size = 5, sort = "id") Pageable pageable,
                       Model model) {
        Page<ServiceResponse> page = (name == null || name.isBlank())
                ? serviceService.getAll(pageable)
                : serviceService.searchByName(name, pageable);

        model.addAttribute("services", page);
        model.addAttribute("keyword", name);
        return "service/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("service", serviceService.getById(id));
        return "service/detail";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("serviceRequest", new ServiceRequest(
                null, null, null, null, null, null, null, null));
        addFormReferenceData(model);
        return "service/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("serviceRequest") ServiceRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addFormReferenceData(model);
            return "service/form";
        }

        try{
            ServiceResponse created = serviceService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo dịch vụ thành công");
            return "redirect:/admin/services/" + created.id();
        } catch (DuplicateResourceException ex) {
            bindingResult.rejectValue("code", "duplicate", ex.getMessage());
            addFormReferenceData(model);
            return "service/form";
        } catch (ResourceNotFoundException ex) {
            bindingResult.rejectValue("departmentId", "invalid", ex.getMessage());
            addFormReferenceData(model);
            return "service/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        ServiceResponse existing = serviceService.getById(id);
        model.addAttribute("serviceRequest", new ServiceRequest(
                existing.name(),
                existing.code(),
                existing.description(),
                existing.category(),
                existing.processingTime(),
                existing.fee(),
                existing.departmentId(),
                existing.assignedStaffId()));
        model.addAttribute("serviceId", id);
        addFormReferenceData(model);
        return "service/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("serviceRequest") ServiceRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("serviceId", id);
            addFormReferenceData(model);
            return "service/form";
        }

        try {
            ServiceResponse updated = serviceService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật dịch vụ thành công");
            return "redirect:/admin/services/" + updated.id();
        } catch (DuplicateResourceException ex) {
            bindingResult.rejectValue("code", "duplicate", ex.getMessage());
            model.addAttribute("serviceId", id);
            addFormReferenceData(model);
            return "service/form";
        } catch (ResourceNotFoundException ex) {
            bindingResult.rejectValue("departmentId", "invalid", ex.getMessage());
            model.addAttribute("serviceId", id);
            addFormReferenceData(model);
            return "service/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            serviceService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa dịch vụ");
        } catch (ServiceInUseException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/services";
    }

    private void addFormReferenceData(Model model) {
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("staffList", staffRepository.findAll());
    }
}