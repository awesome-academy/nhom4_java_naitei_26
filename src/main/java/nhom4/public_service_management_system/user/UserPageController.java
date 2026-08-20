package nhom4.public_service_management_system.user;

import java.util.List;

import jakarta.validation.Valid;
import nhom4.public_service_management_system.enums.UserRole;
import nhom4.public_service_management_system.enums.UserStatus;
import nhom4.public_service_management_system.exception.DuplicateResourceException;
import nhom4.public_service_management_system.user.dto.UserForm;
import nhom4.public_service_management_system.user.dto.UserProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class UserPageController {

    private static final List<UserRole> MANAGED_ROLES = List.of(UserRole.CITIZEN, UserRole.STAFF);

    private final UserService userService;

    public UserPageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String index(
            @RequestParam(required = false) UserRole role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by("id").descending());
        Page<UserProfileResponse> users = userService.findProfiles(role, pageable);
        model.addAttribute("users", users);
        model.addAttribute("selectedRole", role);
        model.addAttribute("roles", MANAGED_ROLES);
        return "users/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        model.addAttribute("roles", MANAGED_ROLES);
            model.addAttribute("statuses", List.of(UserStatus.ACTIVE, UserStatus.LOCKED));
        model.addAttribute("mode", "create");
        return "users/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("userForm") UserForm userForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        validatePasswordForCreate(userForm, bindingResult);
        validateCitizenIdentity(userForm, bindingResult);
        if (bindingResult.hasErrors()) {
            prepareFormModel(model, "create");
            return "users/form";
        }

        try {
            UserProfileResponse created = userService.createWithProfile(userForm);
            redirectAttributes.addFlashAttribute("successMessage", "Da tao tai khoan nguoi dung.");
            return "redirect:/admin/users/" + created.displayId();
        } catch (DuplicateResourceException ex) {
            rejectDuplicate(bindingResult, ex);
            prepareFormModel(model, "create");
            return "users/form";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("globalError", ex.getMessage());
            prepareFormModel(model, "create");
            return "users/form";
        }
    }

    @GetMapping("/{displayId}")
    public String detail(@PathVariable Long displayId, Model model) {
        model.addAttribute("user", userService.findProfileByDisplayId(displayId));
        return "users/detail";
    }

    @GetMapping("/{displayId}/edit")
    public String editForm(@PathVariable Long displayId, Model model) {
        model.addAttribute("userDisplayId", displayId);
        model.addAttribute("userForm", UserForm.from(userService.findProfileByDisplayId(displayId)));
        model.addAttribute("roles", MANAGED_ROLES);
            model.addAttribute("statuses", List.of(UserStatus.ACTIVE, UserStatus.LOCKED));
        model.addAttribute("mode", "edit");
        return "users/form";
    }

    @PostMapping("/{displayId}")
    public String update(
            @PathVariable Long displayId,
            @Valid @ModelAttribute("userForm") UserForm userForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        validateCitizenIdentity(userForm, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("userDisplayId", displayId);
            prepareFormModel(model, "edit");
            return "users/form";
        }

        try {
            userService.updateWithProfileByDisplayId(displayId, userForm);
            redirectAttributes.addFlashAttribute("successMessage", "Da cap nhat thong tin nguoi dung.");
            return "redirect:/admin/users/" + displayId;
        } catch (DuplicateResourceException ex) {
            rejectDuplicate(bindingResult, ex);
            model.addAttribute("userDisplayId", displayId);
            prepareFormModel(model, "edit");
            return "users/form";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("globalError", ex.getMessage());
            model.addAttribute("userDisplayId", displayId);
            prepareFormModel(model, "edit");
            return "users/form";
        }
    }

    @PostMapping("/{displayId}/lock")
    public String lock(@PathVariable Long displayId, RedirectAttributes redirectAttributes) {
        userService.lockByDisplayId(displayId);
        redirectAttributes.addFlashAttribute("successMessage", "Da khoa tai khoan nguoi dung.");
        return "redirect:/admin/users/" + displayId;
    }

    @PostMapping("/{displayId}/delete")
    public String delete(@PathVariable Long displayId, RedirectAttributes redirectAttributes) {
        userService.deleteByDisplayId(displayId);
        redirectAttributes.addFlashAttribute("successMessage", "Da xoa nguoi dung.");
        return "redirect:/admin/users";
    }

    private void prepareFormModel(Model model, String mode) {
        model.addAttribute("roles", MANAGED_ROLES);
            model.addAttribute("statuses", List.of(UserStatus.ACTIVE, UserStatus.LOCKED));
        model.addAttribute("mode", mode);
    }

    private void rejectDuplicate(BindingResult bindingResult, DuplicateResourceException ex) {
        if (ex.getMessage().toLowerCase().contains("email")) {
            bindingResult.rejectValue("email", "duplicate", ex.getMessage());
            return;
        }
        if (ex.getMessage().toLowerCase().contains("cccd")) {
            bindingResult.rejectValue("identityNumber", "duplicate", ex.getMessage());
            return;
        }
        bindingResult.rejectValue("phone", "duplicate", ex.getMessage());
    }

    private void validatePasswordForCreate(UserForm userForm, BindingResult bindingResult) {
        String password = userForm.getPassword();
        if (password == null || password.isBlank()) {
            bindingResult.rejectValue("password", "required", "Mat khau khong duoc de trong");
        } else if (password.length() < 6) {
            bindingResult.rejectValue("password", "size", "Mat khau phai co it nhat 6 ky tu");
        }
    }

    private void validateCitizenIdentity(UserForm userForm, BindingResult bindingResult) {
        if (userForm.getRole() == UserRole.CITIZEN
                && (userForm.getIdentityNumber() == null || userForm.getIdentityNumber().isBlank())) {
            bindingResult.rejectValue("identityNumber", "required", "So CCCD/CMND khong duoc de trong");
        }
    }
}
