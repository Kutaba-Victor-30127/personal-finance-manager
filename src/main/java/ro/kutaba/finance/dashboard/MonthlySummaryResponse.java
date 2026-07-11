package ro.kutaba.finance.dashboard;

import java.math.BigDecimal;

public record MonthlySummaryResponse(
    String month,
    BigDecimal income,
    BigDecimal expenses
) {
}
