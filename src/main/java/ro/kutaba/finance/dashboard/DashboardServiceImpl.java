package ro.kutaba.finance.dashboard;

import ro.kutaba.finance.security.CurrentUserService;
import ro.kutaba.finance.transaction.Transaction;
import ro.kutaba.finance.transaction.TransactionMapper;
import ro.kutaba.finance.transaction.TransactionRepository;
import ro.kutaba.finance.transaction.TransactionResponse;
import ro.kutaba.finance.transaction.TransactionType;
import ro.kutaba.finance.user.User;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

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
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(DashboardFilter filter) {

        List<Transaction> transactions = getTransactionsForCurrentUser(filter);

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
    @Transactional(readOnly = true)
    public List<CategorySummaryResponse> getCategorySummary(DashboardFilter filter) {
        
        List<Transaction> transactions = getTransactionsForCurrentUser(filter);

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

                            long transactionCount = entry.getValue().size();

                        return new CategorySummaryResponse(
                            entry.getKey(),
                            total,
                            transactionCount
                        );
                })
                .sorted(
                    Comparator.comparing(CategorySummaryResponse::total).reversed()
                )

                .toList();  
    }

    @Override
    @Transactional(readOnly = true)
    public List<PeriodSummaryResponse> getPeriodSummary(DashboardFilter filter, GroupBy groupBy) {
        
        List<Transaction> transactions = getTransactionsForCurrentUser(filter);

        Map<LocalDate, List<Transaction>>
            groupedTransactions =
                transactions.stream()

                    .collect(
                        Collectors.groupingBy(

                            transaction ->
                                getPeriodStart(
                                    transaction.getDate(),
                                    groupBy
                                ),

                            TreeMap::new,

                            Collectors.toList()
                        )
                    );
                                
        return groupedTransactions
            .entrySet()
            .stream()

            .map(entry -> {

                BigDecimal income =
                    entry.getValue()
                        .stream()

                        .filter(transaction ->
                            transaction.getType()
                                == TransactionType.INCOME
                        )

                        .map(
                            Transaction::getAmount
                        )

                        .reduce(
                            BigDecimal.ZERO,
                            BigDecimal::add
                        );


                BigDecimal expenses =
                    entry.getValue()
                        .stream()

                        .filter(transaction ->
                            transaction.getType()
                                == TransactionType.EXPENSE
                        )

                        .map(
                            Transaction::getAmount
                        )

                        .reduce(
                            BigDecimal.ZERO,
                            BigDecimal::add
                        );


                return new PeriodSummaryResponse(
                    entry.getKey(),
                    income,
                    expenses
                );
            })

            .toList();

}
      

    @Override
    public List<MonthlySummaryResponse> getMonthlySummary(){

        List<Transaction> transactions = 
                getTransactionsForCurrentUser(
                    new DashboardFilter(
                        null,
                        null,
                        null));

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


    private LocalDate getPeriodStart(
        LocalDate date,
        GroupBy groupBy
) {

    return switch (groupBy) {

        case DAY ->
            date;

        case WEEK ->
            date.with(
                TemporalAdjusters
                    .previousOrSame(
                        DayOfWeek.MONDAY
                    )
            );

        case MONTH ->
            date.withDayOfMonth(1);
    };
}

    private List<Transaction> getTransactionsForCurrentUser(
        DashboardFilter filter
    ) {

    User user =
        currentUserService
            .getCurrentUser();


    return transactionRepository
        .findByUser(user)
        .stream()

        .filter(transaction ->

            filter.startDate() == null ||

            !transaction
                .getDate()
                .isBefore(
                    filter.startDate()
                )
        )

        .filter(transaction ->

            filter.endDate() == null ||

            !transaction
                .getDate()
                .isAfter(
                    filter.endDate()
                )
        )

        .filter(transaction ->

            filter.categoryId() == null ||

            transaction
                .getCategory()
                .getId()
                .equals(
                    filter.categoryId()
                )
        )

        .toList();
    }

}
