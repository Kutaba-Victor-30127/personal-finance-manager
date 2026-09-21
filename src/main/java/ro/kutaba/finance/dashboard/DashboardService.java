package ro.kutaba.finance.dashboard;

import java.util.List;

public interface DashboardService {

    DashboardResponse getDashboard(DashboardFilter filter);

    List<CategorySummaryResponse> getCategorySummary(DashboardFilter filter);

    List<PeriodSummaryResponse> getPeriodSummary(DashboardFilter filter, GroupBy groupBy);

    List<MonthlySummaryResponse> getMonthlySummary();

}