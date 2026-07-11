package ro.kutaba.finance.transaction;

import java.time.LocalDate;

public record TransactionFilter(
    TransactionType type,
    Long categoryId,
    LocalDate startDate,
    LocalDate endDate
) {

}