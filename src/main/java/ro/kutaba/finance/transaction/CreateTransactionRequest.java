package ro.kutaba.finance.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionRequest(
        String title,
        String description,
        BigDecimal amount,
        LocalDate date,
        TransactionType type,
        Long categoryId
) {
}