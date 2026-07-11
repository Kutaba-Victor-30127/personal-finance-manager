package ro.kutaba.finance.dashboard;

import java.math.BigDecimal;
import java.util.List;
import ro.kutaba.finance.transaction.TransactionResponse;

public record DashboardResponse(
    
    BigDecimal totalIncome,
    BigDecimal totalExpenses,
    BigDecimal balance,
    long transactionCount,
    List<TransactionResponse> latestTransactions
) {
    
}
