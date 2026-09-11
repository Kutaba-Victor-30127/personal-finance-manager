package ro.kutaba.finance.transaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import ro.kutaba.finance.category.Category;
import ro.kutaba.finance.category.CategoryRepository;
import ro.kutaba.finance.exception.CategoryNotFoundException;
import ro.kutaba.finance.exception.TransactionNotFoundException;
import ro.kutaba.finance.exception.UnauthorizedException;
import ro.kutaba.finance.security.CurrentUserService;
import ro.kutaba.finance.user.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private TransactionServiceImpl transactionService;


    @Test
    void shouldDeleteTransaction() {

        // Arrange
        User user = new User();
        user.setId(1L);

        Transaction transaction = new Transaction();
        transaction.setId(10L);
        transaction.setUser(user);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(transactionRepository.findById(10L))
                .thenReturn(Optional.of(transaction));

        // Act
        transactionService.delete(10L);

        // Assert
        verify(currentUserService).getCurrentUser();
        verify(transactionRepository).findById(10L);
        verify(transactionRepository).delete(transaction);
    }


    @Test
    void shouldThrowUnauthorizedExceptionWhenDeletingAnotherUsersTransaction() {

        // Arrange
        User currentUser = new User();
        currentUser.setId(1L);

        User owner = new User();
        owner.setId(2L);

        Transaction transaction = new Transaction();
        transaction.setId(10L);
        transaction.setUser(owner);

        when(currentUserService.getCurrentUser())
                .thenReturn(currentUser);

        when(transactionRepository.findById(10L))
                .thenReturn(Optional.of(transaction));

        // Act + Assert
        assertThrows(
                UnauthorizedException.class,
                () -> transactionService.delete(10L)
        );

        verify(transactionRepository).findById(10L);

        verify(transactionRepository, never())
                .delete(any(Transaction.class));
    }


    @Test
    void shouldThrowTransactionNotFoundExceptionWhenDeletingNonExistingTransaction() {

        // Arrange
        User currentUser = new User();
        currentUser.setId(1L);

        when(currentUserService.getCurrentUser())
                .thenReturn(currentUser);

        when(transactionRepository.findById(10L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                TransactionNotFoundException.class,
                () -> transactionService.delete(10L)
        );

        verify(transactionRepository).findById(10L);

        verify(transactionRepository, never())
                .delete(any(Transaction.class));
    }


    @Test
    void shouldCreateTransactionSuccessfully() {

        // Arrange
        User user = new User();
        user.setId(1L);

        Category category = new Category();
        category.setId(1L);
        category.setName("Food");
        category.setUser(user);

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "Lidl",
                        "Weekly groceries",
                        new BigDecimal("185"),
                        LocalDate.of(2026, 1, 5),
                        TransactionType.EXPENSE,
                        1L
                );

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(categoryRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(category));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TransactionResponse response =
                transactionService.create(request);

        // Assert
        ArgumentCaptor<Transaction> captor =
                ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository)
                .save(captor.capture());

        Transaction saved = captor.getValue();

        assertEquals("Lidl", saved.getTitle());
        assertEquals("Weekly groceries", saved.getDescription());
        assertEquals(new BigDecimal("185"), saved.getAmount());
        assertEquals(LocalDate.of(2026, 1, 5), saved.getDate());
        assertEquals(TransactionType.EXPENSE, saved.getType());
        assertEquals(category, saved.getCategory());
        assertEquals(user, saved.getUser());

        assertEquals("Lidl", response.title());

        verify(categoryRepository)
                .findByIdAndUser(1L, user);
    }


    @Test
    void shouldThrowCategoryNotFoundExceptionWhenCreatingTransaction() {

        // Arrange
        User user = new User();
        user.setId(1L);

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "Lidl",
                        "Weekly groceries",
                        new BigDecimal("185"),
                        LocalDate.of(2026, 1, 5),
                        TransactionType.EXPENSE,
                        1L
                );

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(categoryRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                CategoryNotFoundException.class,
                () -> transactionService.create(request)
        );

        verify(categoryRepository)
                .findByIdAndUser(1L, user);

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }


    @Test
    void shouldUpdateTransactionSuccessfully() {

        // Arrange
        User user = new User();
        user.setId(1L);

        Category category = new Category();
        category.setId(2L);
        category.setName("Food");
        category.setUser(user);

        Transaction transaction = new Transaction();
        transaction.setId(10L);
        transaction.setUser(user);

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "Kaufland",
                        "Weekly groceries",
                        new BigDecimal("200"),
                        LocalDate.of(2026, 2, 10),
                        TransactionType.EXPENSE,
                        2L
                );

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(transactionRepository.findById(10L))
                .thenReturn(Optional.of(transaction));

        when(categoryRepository.findByIdAndUser(2L, user))
                .thenReturn(Optional.of(category));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TransactionResponse response =
                transactionService.update(10L, request);

        // Assert
        ArgumentCaptor<Transaction> captor =
                ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository)
                .save(captor.capture());

        Transaction updated = captor.getValue();

        assertEquals("Kaufland", updated.getTitle());
        assertEquals("Weekly groceries", updated.getDescription());
        assertEquals(new BigDecimal("200"), updated.getAmount());
        assertEquals(LocalDate.of(2026, 2, 10), updated.getDate());
        assertEquals(TransactionType.EXPENSE, updated.getType());
        assertEquals(category, updated.getCategory());

        assertEquals("Kaufland", response.title());

        verify(transactionRepository)
                .findById(10L);

        verify(categoryRepository)
                .findByIdAndUser(2L, user);
    }


    @Test
    void shouldThrowUnauthorizedExceptionWhenUpdatingAnotherUsersTransaction() {

        // Arrange
        User currentUser = new User();
        currentUser.setId(1L);

        User owner = new User();
        owner.setId(2L);

        Transaction transaction = new Transaction();
        transaction.setId(10L);
        transaction.setUser(owner);

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "Kaufland",
                        "Weekly groceries",
                        new BigDecimal("200"),
                        LocalDate.of(2026, 2, 10),
                        TransactionType.EXPENSE,
                        2L
                );

        when(currentUserService.getCurrentUser())
                .thenReturn(currentUser);

        when(transactionRepository.findById(10L))
                .thenReturn(Optional.of(transaction));

        // Act + Assert
        assertThrows(
                UnauthorizedException.class,
                () -> transactionService.update(10L, request)
        );

        verify(transactionRepository).findById(10L);

        verify(categoryRepository, never())
                .findByIdAndUser(any(), any());

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }


    @Test
    void shouldThrowTransactionNotFoundExceptionWhenUpdatingNonExistingTransaction() {

        // Arrange
        User user = new User();
        user.setId(1L);

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "Kaufland",
                        "Weekly groceries",
                        new BigDecimal("200"),
                        LocalDate.of(2026, 2, 10),
                        TransactionType.EXPENSE,
                        2L
                );

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(transactionRepository.findById(10L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                TransactionNotFoundException.class,
                () -> transactionService.update(10L, request)
        );

        verify(transactionRepository).findById(10L);

        verify(categoryRepository, never())
                .findByIdAndUser(any(), any());

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }


    @Test
    void shouldReturnTransactionsPage() {

        // Arrange
        User user = new User();
        user.setId(1L);

        Category category = new Category();
        category.setId(1L);
        category.setName("Food");

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setUser(user);
        transaction.setTitle("Lidl");
        transaction.setDescription("Weekly groceries");
        transaction.setAmount(new BigDecimal("200"));
        transaction.setDate(LocalDate.of(2026, 2, 10));
        transaction.setType(TransactionType.EXPENSE);
        transaction.setCategory(category);

        Page<Transaction> page =
                new PageImpl<>(List.of(transaction));

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(transactionRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(page);

        // Act
        Page<TransactionResponse> responsePage =
                transactionService.getAll(
                        0,
                        10,
                        "date",
                        "asc",
                        new TransactionFilter(
                                null,
                                null,
                                null,
                                null
                        )
                );

        // Assert
        assertEquals(1, responsePage.getTotalElements());
        assertEquals("Lidl", responsePage.getContent().get(0).title());

        verify(transactionRepository)
                .findAll(
                        any(Specification.class),
                        any(Pageable.class)
                );
    }


    @Test
    void shouldReturnEmptyPageWhenNoTransactionsExist() {

        // Arrange
        User user = new User();
        user.setId(1L);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(transactionRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        Page<TransactionResponse> result =
                transactionService.getAll(
                        0,
                        10,
                        "date",
                        "desc",
                        new TransactionFilter(
                                null,
                                null,
                                null,
                                null
                        )
                );

        // Assert
        assertTrue(result.isEmpty());

        verify(transactionRepository)
                .findAll(
                        any(Specification.class),
                        any(Pageable.class)
                );
    }


    @Test
    void shouldThrowCategoryNotFoundExceptionWhenUpdatingTransaction() {

        // Arrange
        User user = new User();
        user.setId(1L);

        Transaction transaction = new Transaction();
        transaction.setId(10L);
        transaction.setUser(user);

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "Kaufland",
                        "Weekly groceries",
                        new BigDecimal("200"),
                        LocalDate.of(2026, 2, 10),
                        TransactionType.EXPENSE,
                        2L
                );

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(transactionRepository.findById(10L))
                .thenReturn(Optional.of(transaction));

        when(categoryRepository.findByIdAndUser(2L, user))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                CategoryNotFoundException.class,
                () -> transactionService.update(10L, request)
        );

        verify(transactionRepository)
                .findById(10L);

        verify(categoryRepository)
                .findByIdAndUser(2L, user);

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }


    @Test
    void shouldAssignCurrentUserToTransactionWhenCreating() {

        // Arrange
        User user = new User();
        user.setId(5L);

        Category category = new Category();
        category.setId(1L);
        category.setName("Salary");
        category.setUser(user);

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "Salary",
                        "Google",
                        new BigDecimal("8000"),
                        LocalDate.of(2026, 9, 10),
                        TransactionType.INCOME,
                        1L
                );

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(categoryRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(category));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        transactionService.create(request);

        // Assert
        ArgumentCaptor<Transaction> captor =
                ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository)
                .save(captor.capture());

        assertEquals(
                user,
                captor.getValue().getUser()
        );
    }


    @Test
    void shouldAssignCategoryToTransactionWhenCreating() {

        // Arrange
        User user = new User();
        user.setId(1L);

        Category category = new Category();
        category.setId(7L);
        category.setName("Shopping");
        category.setUser(user);

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "Nike",
                        "Shoes",
                        new BigDecimal("450"),
                        LocalDate.of(2026, 9, 10),
                        TransactionType.EXPENSE,
                        7L
                );

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(categoryRepository.findByIdAndUser(7L, user))
                .thenReturn(Optional.of(category));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        transactionService.create(request);

        // Assert
        ArgumentCaptor<Transaction> captor =
                ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository)
                .save(captor.capture());

        assertEquals(
                category,
                captor.getValue().getCategory()
        );
    }


    @Test
    void shouldUseAscendingSort() {

        // Arrange
        User user = new User();
        user.setId(1L);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(transactionRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        transactionService.getAll(
                0,
                10,
                "amount",
                "asc",
                new TransactionFilter(
                        null,
                        null,
                        null,
                        null
                )
        );

        // Assert
        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(transactionRepository)
                .findAll(
                        any(Specification.class),
                        captor.capture()
                );

        Pageable pageable = captor.getValue();

        assertEquals(
                Sort.Direction.ASC,
                pageable
                        .getSort()
                        .getOrderFor("amount")
                        .getDirection()
        );
    }


    @Test
    void shouldUseDescendingSort() {

        // Arrange
        User user = new User();
        user.setId(1L);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(transactionRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        transactionService.getAll(
                0,
                10,
                "date",
                "desc",
                new TransactionFilter(
                        null,
                        null,
                        null,
                        null
                )
        );

        // Assert
        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(transactionRepository)
                .findAll(
                        any(Specification.class),
                        captor.capture()
                );

        Pageable pageable = captor.getValue();

        assertEquals(
                Sort.Direction.DESC,
                pageable
                        .getSort()
                        .getOrderFor("date")
                        .getDirection()
        );
    }


    @Test
    void shouldUseCorrectPageNumber() {

        // Arrange
        User user = new User();
        user.setId(1L);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(transactionRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        transactionService.getAll(
                2,
                5,
                "date",
                "desc",
                new TransactionFilter(
                        null,
                        null,
                        null,
                        null
                )
        );

        // Assert
        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(transactionRepository)
                .findAll(
                        any(Specification.class),
                        captor.capture()
                );

        Pageable pageable = captor.getValue();

        assertEquals(
                2,
                pageable.getPageNumber()
        );

        assertEquals(
                5,
                pageable.getPageSize()
        );
    }
}