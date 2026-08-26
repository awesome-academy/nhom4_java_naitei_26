package nhom4.public_service_management_system.department;

import java.beans.PropertyEditorSupport;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import nhom4.public_service_management_system.department.dto.DepartmentForm;
import nhom4.public_service_management_system.department.dto.DepartmentResponse;
import nhom4.public_service_management_system.exception.DuplicateResourceException;
import nhom4.public_service_management_system.exception.ResourceNotFoundException;
import nhom4.public_service_management_system.staff.StaffEntity;
import nhom4.public_service_management_system.staff.StaffRepository;

@Controller
@RequestMapping("/admin/departments")
public class DepartmentPageController {

    private final DepartmentService departmentService;
    private final StaffRepository staffRepository;

    public DepartmentPageController(DepartmentService departmentService, StaffRepository staffRepository) {
        this.departmentService = departmentService;
        this.staffRepository = staffRepository;
    }

    @InitBinder("departmentForm")
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Long.class, "leaderStaffId", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue((text == null || text.isBlank()) ? null : Long.valueOf(text.trim()));
            }
        });
    }

    @GetMapping
    public String index(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by("id").ascending());
        Page<DepartmentResponse> departments = (name == null || name.isBlank())
                ? departmentService.getAll(pageable)
                : departmentService.searchByName(name, pageable);

        model.addAttribute("departments", departments);
        model.addAttribute("keyword", name);
        return "departments/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        DepartmentResponse department = departmentService.getById(id);
        List<StaffEntity> staffList = staffRepository.findByDepartmentId(id);
        model.addAttribute("department", department);
        model.addAttribute("staffList", staffList);
        return "departments/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("departmentForm", new DepartmentForm());
        model.addAttribute("staffList", staffRepository.findByDepartmentIdIsNull());
        model.addAttribute("mode", "create");
        return "departments/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("departmentForm") DepartmentForm departmentForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareFormModel(model, "create", null);
            return "departments/form";
        }

        try {
            departmentService.create(departmentForm.toRequest());
            redirectAttributes.addFlashAttribute("sucessMessage", "Đã tạo phòng ban mới.");
            return "redirect:/admin/departments";
        } catch (DuplicateResourceException ex) {
            bindingResult.rejectValue("code", "duplicate", ex.getMessage());
            prepareFormModel(model, "create", null);
            return "departments/form";
        } catch (ResourceNotFoundException ex) {
            bindingResult.rejectValue("leaderStaffId", "invalid", ex.getMessage());
            prepareFormModel(model, "create", null);
            return "departments/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("departmentId", id);
        model.addAttribute("departmentForm", DepartmentForm.from(departmentService.getById(id)));
        model.addAttribute("staffList", staffRepository.findByDepartmentId(id));
        model.addAttribute("mode", "edit");
        return "departments/form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("departmentForm") DepartmentForm departmentForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departmentId", id);
            prepareFormModel(model, "edit", id);
            return "departments/form";
        }

        try {
            departmentService.update(id, departmentForm.toRequest());
            redirectAttributes.addFlashAttribute("sucessMessage", "Đã cập nhật phòng ban.");
            return "redirect:/admin/departments";
        } catch (DuplicateResourceException ex) {
            bindingResult.rejectValue("code", "duplicate", ex.getMessage());
            model.addAttribute("departmentId", id);
            prepareFormModel(model, "edit", id);
            return "departments/form";
        } catch (ResourceNotFoundException ex) {
            bindingResult.rejectValue("leaderStaffId", "invalid", ex.getMessage());
            model.addAttribute("departmentId", id);
            prepareFormModel(model, "edit", id);
            return "departments/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        departmentService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa phòng ban.");
        return "redirect:/admin/departments";
    }

    private void prepareFormModel(Model model, String mode, Long departmentId) {
        if (departmentId != null) {
            model.addAttribute("staffList", staffRepository.findByDepartmentId(departmentId));
        } else {
            model.addAttribute("staffList", staffRepository.findByDepartmentIdIsNull());
        }
        model.addAttribute("mode", mode);
    }

}