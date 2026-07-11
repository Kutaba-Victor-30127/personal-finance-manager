package ro.kutaba.finance.dashboard;

import java.math.BigDecimal;

public record CategorySummaryResponse(
    String category,
    BigDecimal total
){
}
