package ro.kutaba.finance.dashboard;

import java.time.LocalDate;

public record DashboardFilter(
        LocalDate startDate,
        LocalDate endDate,
        Long categoryId
) {
}