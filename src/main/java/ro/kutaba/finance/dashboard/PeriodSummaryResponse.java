package ro.kutaba.finance.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PeriodSummaryResponse(

    LocalDate periodStart,

    BigDecimal income,

    BigDecimal expenses

) {
} 