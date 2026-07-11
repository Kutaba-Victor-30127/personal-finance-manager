package ro.kutaba.finance.dashboard;

import ro.kutaba.finance.exception.UserNotFoundException;
import ro.kutaba.finance.security.CurrentUserService;
import ro.kutaba.finance.transaction.Transaction;
import ro.kutaba.finance.transaction.TransactionMapper;
import ro.kutaba.finance.transaction.TransactionRepository;
import ro.kutaba.finance.transaction.TransactionResponse;
import ro.kutaba.finance.transaction.TransactionType;
import ro.kutaba.finance.user.User;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final TransactionRepository transactionRepository;
    private final CurrentUserService currentUserService;

    public DashboardServiceImpl(TransactionRepository transactionRepository, CurrentUserService currentUserService){
        this.transactionRepository = transactionRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public DashboardResponse getDashboard(){

        List<Transaction> transactions = getTransactionsForCurrentUser();

        BigDecimal totalIncome = transactions.stream()
                                    .filter(transaction -> transaction.getType() == TransactionType.INCOME)
                                    .map(Transaction::getAmount)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

    
        BigDecimal totalExpenses = transactions.stream()
                                    .filter(transaction -> transaction.getType() == TransactionType.EXPENSE)
                                    .map(Transaction::getAmount)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = totalIncome.subtract(totalExpenses);

        long transactionCount = transactions.size();

        List<TransactionResponse> latestTransactions = transactions.stream()
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .limit(5)
                .map(TransactionMapper::toResponse)
                .toList();

        return new DashboardResponse(totalIncome, totalExpenses, balance, transactionCount, latestTransactions);
    }

    @Override
    public List<CategorySummaryResponse> getCategorySummary() {
        
        List<Transaction> transactions = getTransactionsForCurrentUser();

        Map<String, List<Transaction>> groupedTransactions = transactions.stream()
                .filter(transaction -> transaction.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                    transaction -> transaction.getCategory().getName()));

        return groupedTransactions.entrySet()
                .stream()
                .map(entry -> {
                    BigDecimal total = entry.getValue()
                            .stream()
                            .map(Transaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                        return new CategorySummaryResponse(
                            entry.getKey(),
                            total
                        );
                })
                .toList();  
    }

    @Override
    public List<MonthlySummaryResponse> getMonthlySummary(){

        List<Transaction> transactions = getTransactionsForCurrentUser();

        Map<YearMonth, List<Transaction>> monthlyTransactions = transactions.stream()
                .collect(Collectors.groupingBy(
                    transaction -> YearMonth.from(transaction.getDate()),
                    TreeMap::new, 
                    Collectors.toList()
                ));

        return monthlyTransactions.entrySet()
                .stream()
                .map(entry -> {
                    BigDecimal income = entry.getValue()
                        .stream()
                        .filter(transaction -> transaction.getType() == TransactionType.INCOME)
                        .map(Transaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                        
                    BigDecimal expenses = entry.getValue()
                        .stream()
                        .filter(transaction -> transaction.getType() == TransactionType.EXPENSE)
                        .map(Transaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new MonthlySummaryResponse(
                        entry.getKey().toString(),
                        income,
                        expenses
                    );
                })
                .toList();
    }

    private List<Transaction> getTransactionsForCurrentUser(){
        User user = currentUserService.getCurrentUser();
        return transactionRepository.findByUser(user);
    }
}
