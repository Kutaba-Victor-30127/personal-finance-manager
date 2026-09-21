package ro.kutaba.finance.dashboard;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardResponse getDashboard(DashboardFilter filter) {
        
        return dashboardService.getDashboard(filter);
    }

    @GetMapping("/category-summary")
    public List<CategorySummaryResponse> getCategorySummary(DashboardFilter filter) {
        
        return dashboardService.getCategorySummary(filter);
    }

    @GetMapping("/period-summary")
    public List<PeriodSummaryResponse> getPeriodSummary(
        DashboardFilter filter, 
        @RequestParam(defaultValue = "MONTH") GroupBy groupBy) {
        
        return dashboardService.getPeriodSummary(filter, groupBy);
    }

    @GetMapping("/monthly-summary")
    public List<MonthlySummaryResponse> getMonthlySummary() {
        
        return dashboardService.getMonthlySummary();
    }
}