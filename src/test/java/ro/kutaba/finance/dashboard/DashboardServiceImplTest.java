package ro.kutaba.finance.dashboard;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ro.kutaba.finance.category.CategoryRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import ro.kutaba.finance.user.User;
import ro.kutaba.finance.security.CurrentUserService;
import ro.kutaba.finance.transaction.TransactionRepository;
import ro.kutaba.finance.transaction.TransactionType;
import ro.kutaba.finance.transaction.Transaction;
import ro.kutaba.finance.category.Category;
import ro.kutaba.finance.exception.CategoryNotFoundException;
import ro.kutaba.finance.exception.TransactionNotFoundException;
import ro.kutaba.finance.exception.UnauthorizedException;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Test
    void shouldCalculateDashboardTotals() {

        //Arrange
        User user = new User();
        user.setId(1L);

        Category salary = new Category();
        salary.setName("Salary");

        Category food = new Category();
        food.setName("Food");

        Transaction income1 = new Transaction();
        income1.setAmount(new BigDecimal("5000"));
        income1.setType(TransactionType.INCOME);
        income1.setCategory(salary);
        income1.setUser(user);
        income1.setDate(LocalDate.of(2026,1,5));

        Transaction income2 = new Transaction();
        income2.setAmount(new BigDecimal("2000"));
        income2.setType(TransactionType.INCOME);
        income2.setCategory(salary);
        income2.setUser(user);
        income2.setDate(LocalDate.of(2026,1,10));

        Transaction expense1 = new Transaction();
        expense1.setAmount(new BigDecimal("300"));
        expense1.setType(TransactionType.EXPENSE);
        expense1.setCategory(food);
        expense1.setUser(user);
        expense1.setDate(LocalDate.of(2026,1,11));

        Transaction expense2 = new Transaction();
        expense2.setAmount(new BigDecimal("700"));
        expense2.setType(TransactionType.EXPENSE);
        expense2.setCategory(food);
        expense2.setUser(user);
        expense2.setDate(LocalDate.of(2026,1,20));

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(transactionRepository.findByUser(user))
                .thenReturn(List.of(
                    income1, income2, expense1, expense2
                ));

        //Act
        DashboardResponse response = dashboardService.getDashboard();

        //Assert
        assertEquals(new BigDecimal("7000"), response.totalIncome());

        assertEquals(new BigDecimal("1000"), response.totalExpenses());

        assertEquals(new BigDecimal("6000"), response.balance());

        assertEquals(4, response.transactionCount());

    }

    @Test
    void shouldReturnLatestFiveTransactions() {

        // Arrange
        User user = new User();
        user.setId(1L);

        Category category = new Category();
        category.setId(1L);
        category.setName("Food");

        Transaction t1 = new Transaction();
        t1.setTitle("T1");
        t1.setAmount(BigDecimal.ONE);
        t1.setType(TransactionType.EXPENSE);
        t1.setCategory(category);
        t1.setUser(user);
        t1.setDate(LocalDate.of(2026, 2, 1));

        Transaction t2 = new Transaction();
        t2.setTitle("T2");
        t2.setAmount(BigDecimal.ONE);
        t2.setType(TransactionType.EXPENSE);
        t2.setCategory(category);
        t2.setUser(user);
        t2.setDate(LocalDate.of(2026, 2, 2));

        Transaction t3 = new Transaction();
        t3.setTitle("T3");
        t3.setAmount(BigDecimal.ONE);
        t3.setType(TransactionType.EXPENSE);
        t3.setCategory(category);
        t3.setUser(user);
        t3.setDate(LocalDate.of(2026, 2, 3));

        Transaction t4 = new Transaction();
        t4.setTitle("T4");
        t4.setAmount(BigDecimal.ONE);
        t4.setType(TransactionType.EXPENSE);
        t4.setCategory(category);
        t4.setUser(user);
        t4.setDate(LocalDate.of(2026, 2, 4));

        Transaction t5 = new Transaction();
        t5.setTitle("T5");
        t5.setAmount(BigDecimal.ONE);
        t5.setType(TransactionType.EXPENSE);
        t5.setCategory(category);
        t5.setUser(user);
        t5.setDate(LocalDate.of(2026, 2, 5));

        Transaction t6 = new Transaction();
        t6.setTitle("T6");
        t6.setAmount(BigDecimal.ONE);
        t6.setType(TransactionType.EXPENSE);
        t6.setCategory(category);
        t6.setUser(user);
        t6.setDate(LocalDate.of(2026, 2, 6));

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(transactionRepository.findByUser(user))
                .thenReturn(List.of(
                        t3,
                        t1,
                        t6,
                        t2,
                        t5,
                        t4
                ));

        // Act
        DashboardResponse response = dashboardService.getDashboard();

        // Assert
        assertEquals(5, response.latestTransactions().size());

        assertEquals("T6", response.latestTransactions().get(0).title());
        assertEquals("T5", response.latestTransactions().get(1).title());
        assertEquals("T4", response.latestTransactions().get(2).title());
        assertEquals("T3", response.latestTransactions().get(3).title());
        assertEquals("T2", response.latestTransactions().get(4).title());

        assertEquals(
                "Food",
                response.latestTransactions().getFirst().categoryName()
        );

        assertFalse(
                response.latestTransactions()
                        .stream()
                        .anyMatch(t -> t.title().equals("T1"))
        );
    }

    @Test 
    void shouldReturnEmptyDashboardWhenUserHasNoTransactions() {

        //Arrange
        User user = new User();
        user.setId(1L);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(transactionRepository.findByUser(user))
                .thenReturn(List.of());

        //Act
        DashboardResponse response = dashboardService.getDashboard();

        //Assert
        assertEquals(BigDecimal.ZERO, response.totalIncome());
        assertEquals(BigDecimal.ZERO, response.totalExpenses());
        assertEquals(BigDecimal.ZERO, response.balance());
        assertEquals(0, response.transactionCount());
        assertTrue(response.latestTransactions().isEmpty());
    }

    @Test
    void shouldGroupExpensesByCategory() {

        //Arrange
        User user = new User();
        user.setId(1L);

        Category food = new Category();
        food.setName("Food");

        Category gym = new Category();
        gym.setName("Gym");

        Transaction t1 = new Transaction();
        t1.setAmount(new BigDecimal("100"));
        t1.setType(TransactionType.EXPENSE);
        t1.setCategory(food);
        t1.setUser(user);

        Transaction t2 = new Transaction();
        t2.setAmount(new BigDecimal("200"));
        t2.setType(TransactionType.EXPENSE);
        t2.setCategory(food);
        t2.setUser(user);

        Transaction t3 = new Transaction();
        t3.setAmount(new BigDecimal("200"));
        t3.setType(TransactionType.EXPENSE);
        t3.setCategory(gym);
        t3.setUser(user);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(transactionRepository.findByUser(user))
                .thenReturn(List.of(t1, t2, t3));

        //Act
        List<CategorySummaryResponse> categorySummary = dashboardService.getCategorySummary();

        //Assert
        assertEquals(2, categorySummary.size());    

        CategorySummaryResponse foodSummary =
                    categorySummary.stream()
                                .filter(r -> r.category().equals("Food"))
                                .findFirst()
                                .orElseThrow();

        assertEquals(new BigDecimal("300"), foodSummary.total());

        CategorySummaryResponse gymSummary =
                    categorySummary.stream()
                                .filter(r -> r.category().equals("Gym"))
                                .findFirst()
                                .orElseThrow();

        assertEquals(new BigDecimal("200"), gymSummary.total());
    }

    @Test
    void shouldIgnoreIncomeTransactions(){

        User user = new User();
        user.setId(1L);

        Category food = new Category();
        food.setName("Food");

        Category salary = new Category();
        salary.setName("Salary");

        Transaction expense1 = new Transaction();
        expense1.setAmount(new BigDecimal("100"));
        expense1.setType(TransactionType.EXPENSE);
        expense1.setCategory(food);
        expense1.setUser(user);

        Transaction expense2 = new Transaction();
        expense2.setAmount(new BigDecimal("200"));
        expense2.setType(TransactionType.EXPENSE);
        expense2.setCategory(food);
        expense2.setUser(user);

        Transaction income = new Transaction();
        income.setAmount(new BigDecimal("5000"));
        income.setType(TransactionType.INCOME);
        income.setCategory(salary);
        income.setUser(user);

        when(currentUserService.getCurrentUser())
        .thenReturn(user);

        when(transactionRepository.findByUser(user))
                .thenReturn(List.of(
                        expense1,
                        expense2,
                        income
                ));

        List<CategorySummaryResponse> response = dashboardService.getCategorySummary();

        assertEquals(1, response.size());

        assertEquals(
                "Food",
                response.getFirst().category()
            );

        assertEquals(
                new BigDecimal("300"),
                response.getFirst().total()
            );

        assertFalse(
            response.stream()
                    .anyMatch(r -> r.category().equals("Salary"))
                );

    }

    @Test
    void shouldCalculateCategoryTotals() {

        User user = new User();
        user.setId(1L);

        Category food = new Category();
        food.setName("Food");

        Transaction t1 = new Transaction();
        t1.setAmount(new BigDecimal("100"));
        t1.setType(TransactionType.EXPENSE);
        t1.setCategory(food);
        t1.setUser(user);

        Transaction t2 = new Transaction();
        t2.setAmount(new BigDecimal("200"));
        t2.setType(TransactionType.EXPENSE);
        t2.setCategory(food);
        t2.setUser(user);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(transactionRepository.findByUser(user))
                .thenReturn(List.of(t1, t2));

        List<CategorySummaryResponse> response =
                dashboardService.getCategorySummary();

        assertEquals(1, response.size());
        assertEquals("Food", response.getFirst().category());
        assertEquals(new BigDecimal("300"), response.getFirst().total());
    }

    @Test
    void shouldCalculateMonthlySummary() {

        User user = new User();
        user.setId(1L);

        Category salary = new Category();
        salary.setName("Salary");

        Category food = new Category();
        food.setName("Food");

        Transaction income = new Transaction();
        income.setAmount(new BigDecimal("5000"));
        income.setType(TransactionType.INCOME);
        income.setCategory(salary);
        income.setUser(user);
        income.setDate(LocalDate.of(2026,1,5));

        Transaction expense = new Transaction();
        expense.setAmount(new BigDecimal("1200"));
        expense.setType(TransactionType.EXPENSE);
        expense.setCategory(food);
        expense.setUser(user);
        expense.setDate(LocalDate.of(2026,1,10));

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(transactionRepository.findByUser(user))
                .thenReturn(List.of(income, expense));

        List<MonthlySummaryResponse> response =
                dashboardService.getMonthlySummary();

        assertEquals(1, response.size());

        assertEquals("2026-01", response.getFirst().month());

        assertEquals(
                new BigDecimal("5000"),
                response.getFirst().income()
        );

        assertEquals(
                new BigDecimal("1200"),
                response.getFirst().expenses()
        );
    }

    @Test
    void shouldReturnMonthsInChronologicalOrder() {

        User user = new User();
        user.setId(1L);

        Category category = new Category();
        category.setName("Food");

        Transaction january = new Transaction();
        january.setAmount(BigDecimal.ONE);
        january.setType(TransactionType.EXPENSE);
        january.setCategory(category);
        january.setUser(user);
        january.setDate(LocalDate.of(2026,1,10));

        Transaction march = new Transaction();
        march.setAmount(BigDecimal.ONE);
        march.setType(TransactionType.EXPENSE);
        march.setCategory(category);
        march.setUser(user);
        march.setDate(LocalDate.of(2026,3,10));

        Transaction february = new Transaction();
        february.setAmount(BigDecimal.ONE);
        february.setType(TransactionType.EXPENSE);
        february.setCategory(category);
        february.setUser(user);
        february.setDate(LocalDate.of(2026,2,10));

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(transactionRepository.findByUser(user))
                .thenReturn(List.of(
                        march,
                        january,
                        february
                ));

        List<MonthlySummaryResponse> response =
                dashboardService.getMonthlySummary();

        assertEquals("2026-01", response.get(0).month());
        assertEquals("2026-02", response.get(1).month());
        assertEquals("2026-03", response.get(2).month());
    }

    @Test
    void shouldReturnEmptyCategorySummary() {

        User user = new User();
        user.setId(1L);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(transactionRepository.findByUser(user))
                .thenReturn(List.of());

        List<CategorySummaryResponse> response =
                dashboardService.getCategorySummary();

        assertTrue(response.isEmpty());
    }

    @Test
    void shouldReturnEmptyMonthlySummary() {

        User user = new User();
        user.setId(1L);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(transactionRepository.findByUser(user))
                .thenReturn(List.of());

        List<MonthlySummaryResponse> response =
                dashboardService.getMonthlySummary();

        assertTrue(response.isEmpty());
    }


}
