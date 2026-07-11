package ro.kutaba.finance.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateTransactionRequest(
        @NotBlank(message = "Title is required")
        String title,
        
        String description, 
        
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        @NotNull(message = "Date is required")
        LocalDate date,

        @NotNull(message = "Type is required")
        TransactionType type,

        @NotNull(message = "Category is required")
        Long categoryId
) {
}