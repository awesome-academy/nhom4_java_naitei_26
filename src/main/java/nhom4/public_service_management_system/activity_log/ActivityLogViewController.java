package nhom4.public_service_management_system.activity_log;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/activity-logs")
public class ActivityLogViewController {

    private final ActivityLogService activityLogService;

    public ActivityLogViewController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<?> logPage = activityLogService.findAll(pageable);

        model.addAttribute("logPage", logPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", logPage.getTotalPages());

        return "activity-logs";
    }
}