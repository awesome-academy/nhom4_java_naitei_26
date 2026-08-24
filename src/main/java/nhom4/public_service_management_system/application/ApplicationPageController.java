package nhom4.public_service_management_system.application;

import jakarta.validation.Valid;
import nhom4.public_service_management_system.application.dto.ApplicationForm;
import nhom4.public_service_management_system.application.dto.ApplicationResponse;
import nhom4.public_service_management_system.application_document.ApplicationDocumentRepository;
import nhom4.public_service_management_system.application_history.ApplicationHistoryRepository;
import nhom4.public_service_management_system.auth.CustomUserDetails;
import nhom4.public_service_management_system.citizen.CitizenRepository;
import nhom4.public_service_management_system.enums.ApplicationStatus;
import nhom4.public_service_management_system.exception.ResourceNotFoundException;
import nhom4.public_service_management_system.service.ServiceEntity;
import nhom4.public_service_management_system.service.ServiceRepository;
import nhom4.public_service_management_system.staff.StaffRepository;
import nhom4.public_service_management_system.user.UserEntity;
import nhom4.public_service_management_system.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/applications")
public class ApplicationPageController {

    private final ApplicationService applicationService;
    private final CitizenRepository citizenRepository;
    private final ServiceRepository serviceRepository;
    private final StaffRepository staffRepository;
    private final ApplicationDocumentRepository applicationDocumentRepository;
    private final ApplicationHistoryRepository applicationHistoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public ApplicationPageController(
            ApplicationService applicationService,
            CitizenRepository citizenRepository,
            ServiceRepository serviceRepository,
            StaffRepository staffRepository,
            ApplicationDocumentRepository applicationDocumentRepository,
            ApplicationHistoryRepository applicationHistoryRepository,
            UserRepository userRepository) {
        this.applicationService = applicationService;
        this.citizenRepository = citizenRepository;
        this.serviceRepository = serviceRepository;
        this.staffRepository = staffRepository;
        this.applicationDocumentRepository = applicationDocumentRepository;
        this.applicationHistoryRepository = applicationHistoryRepository;
        this.userRepository = userRepository;
    }

    @InitBinder("applicationForm")
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Long.class, "citizenId", new CustomNumberEditor(Long.class, true));
        binder.registerCustomEditor(Long.class, "serviceId", new CustomNumberEditor(Long.class, true));
        binder.registerCustomEditor(Long.class, "assignedStaffId", new CustomNumberEditor(Long.class, true));
    }

    @GetMapping
    public String index(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by("id").descending());
        Page<ApplicationResponse> applications = applicationService.search(keyword, status, pageable);

        model.addAttribute("applications", applications);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", ApplicationStatus.values());

        // Status counts for statistic pills
        model.addAttribute("totalCount", applicationService.countByStatus(null));
        model.addAttribute("receivedCount", applicationService.countByStatus(ApplicationStatus.RECEIVED));
        model.addAttribute("processingCount", applicationService.countByStatus(ApplicationStatus.PROCESSING));
        model.addAttribute("approvedCount", applicationService.countByStatus(ApplicationStatus.APPROVED));
        model.addAttribute("rejectedCount", applicationService.countByStatus(ApplicationStatus.REJECTED));

        return "applications/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        ApplicationResponse application = applicationService.getById(id);
        ApplicationEntity entity = applicationService.getEntityById(id);

        model.addAttribute("application", application);
        model.addAttribute("applicationEntity", entity);
        model.addAttribute("citizen", entity.getCitizen());

        if (entity.getServiceId() != null) {
            ServiceEntity service = serviceRepository.findById(entity.getServiceId()).orElse(null);
            model.addAttribute("service", service);
        }

        if (entity.getAssignedStaffId() != null) {
            staffRepository.findById(entity.getAssignedStaffId())
                    .ifPresent(staff -> model.addAttribute("assignedStaff", staff));
        }

        model.addAttribute("documents", applicationDocumentRepository.findByApplicationId(id));
        model.addAttribute("histories", applicationHistoryRepository.findByApplicationIdOrderByChangedAtDesc(id));
        model.addAttribute("staffList", staffRepository.findAll(Sort.by("name").ascending()));
        model.addAttribute("statuses", ApplicationStatus.values());

        return "applications/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("applicationForm", new ApplicationForm());
        prepareFormModel(model, "create");
        return "applications/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("applicationForm") ApplicationForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            prepareFormModel(model, "create");
            return "applications/form";
        }

        try {
            ApplicationResponse created = applicationService.createFromForm(form);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo hồ sơ thành công với mã: " + created.getApplicationCode());
            return "redirect:/admin/applications/" + created.getId();
        } catch (ResourceNotFoundException ex) {
            bindingResult.reject("error.notfound", ex.getMessage());
            prepareFormModel(model, "create");
            return "applications/form";
        } catch (Exception ex) {
            bindingResult.reject("error.general", "Có lỗi xảy ra: " + ex.getMessage());
            prepareFormModel(model, "create");
            return "applications/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        ApplicationResponse application = applicationService.getById(id);
        model.addAttribute("applicationId", id);
        model.addAttribute("applicationForm", ApplicationForm.from(application));
        model.addAttribute("applicationCode", application.getApplicationCode());
        prepareFormModel(model, "edit");
        return "applications/form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("applicationForm") ApplicationForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("applicationId", id);
            prepareFormModel(model, "edit");
            return "applications/form";
        }

        try {
            applicationService.update(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công.");
            return "redirect:/admin/applications/" + id;
        } catch (ResourceNotFoundException ex) {
            bindingResult.reject("error.notfound", ex.getMessage());
            model.addAttribute("applicationId", id);
            prepareFormModel(model, "edit");
            return "applications/form";
        } catch (Exception ex) {
            bindingResult.reject("error.general", "Có lỗi xảy ra: " + ex.getMessage());
            model.addAttribute("applicationId", id);
            prepareFormModel(model, "edit");
            return "applications/form";
        }
    }

    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status,
            @RequestParam(required = false) String resultNote,
            @RequestParam(required = false) String rejectionReason,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        UserEntity currentUser = null;
        if (userDetails != null && userDetails.getId() != null) {
            currentUser = userRepository.findById(userDetails.getId()).orElse(null);
        }

        applicationService.updateStatus(id, status, resultNote, rejectionReason, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái hồ sơ thành công.");
        return "redirect:/admin/applications/" + id;
    }

    @PostMapping("/{id}/assign")
    public String assignStaff(
            @PathVariable Long id,
            @RequestParam(required = false) Long staffId,
            RedirectAttributes redirectAttributes) {

        applicationService.assignStaff(id, staffId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật cán bộ phụ trách hồ sơ.");
        return "redirect:/admin/applications/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            applicationService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa hồ sơ thành công.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa hồ sơ: " + ex.getMessage());
        }
        return "redirect:/admin/applications";
    }

    private void prepareFormModel(Model model, String mode) {
        model.addAttribute("citizens", citizenRepository.findAll(Sort.by("name").ascending()));
        model.addAttribute("services", serviceRepository.findAll(Sort.by("name").ascending()));
        model.addAttribute("staffList", staffRepository.findAll(Sort.by("name").ascending()));
        model.addAttribute("statuses", ApplicationStatus.values());
        model.addAttribute("mode", mode);
    }
}
