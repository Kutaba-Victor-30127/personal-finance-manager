package ro.kutaba.finance.dashboard;

import java.util.List;

public interface DashboardService {

    DashboardResponse getDashboard();

    List<CategorySummaryResponse> getCategorySummary();

    List<MonthlySummaryResponse> getMonthlySummary();

}