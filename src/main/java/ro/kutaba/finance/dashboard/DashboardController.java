package ro.kutaba.finance.dashboard;

import org.hibernate.grammars.hql.HqlParser.CaseListContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardResponse getDashboard() {
        return dashboardService.getDashboard();
    }

    @GetMapping("/category-summary")
    public List<CategorySummaryResponse> getCategorySummary() {
        return dashboardService.getCategorySummary();
    }

    @GetMapping("/monthly-summary")
    public List<MonthlySummaryResponse> getMonthlySummary() {
        return dashboardService.getMonthlySummary();
    }
}