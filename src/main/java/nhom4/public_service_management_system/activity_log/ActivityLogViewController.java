package nhom4.public_service_management_system.activity_log;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/activity-logs")
public class ActivityLogViewController {

    @GetMapping
    public String getPage() {
        return "activity-logs";
    }
}