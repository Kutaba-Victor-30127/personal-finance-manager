package ro.kutaba.finance.transaction;

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
import ro.kutaba.finance.category.Category;
import ro.kutaba.finance.exception.CategoryNotFoundException;
import ro.kutaba.finance.exception.TransactionNotFoundException;
import ro.kutaba.finance.exception.UnauthorizedException;

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
    verify(transactionRepository).delete(transaction);
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenDeletingAnotherUsersTransaction(){

        //Arange
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

        verify(transactionRepository, never())
                .delete(any(Transaction.class));
    }

    @Test
    void shouldThrowTransactionNotFoundExceptionWhenDeletingNonExistingTransaction(){

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

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TransactionResponse response =
                transactionService.create(request);

        // Assert
        ArgumentCaptor<Transaction> captor =
                ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository).save(captor.capture());

        Transaction saved = captor.getValue();

        assertEquals("Lidl", saved.getTitle());
        assertEquals("Weekly groceries", saved.getDescription());
        assertEquals(new BigDecimal("185"), saved.getAmount());
        assertEquals(TransactionType.EXPENSE, saved.getType());
        assertEquals(category, saved.getCategory());
        assertEquals(user, saved.getUser());

        assertEquals("Lidl", response.title());
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

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                CategoryNotFoundException.class,
                () -> transactionService.create(request)
        );

        verify(transactionRepository, never())
                .save(any(Transaction.class));
        }

        
     @Test
     void shouldUpdateTransactionSuccessfully(){

        User user = new User();
        user.setId(1L);

        Category category = new Category();
        category.setId(2L);
        category.setName("Food");

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

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(category));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TransactionResponse response = transactionService.update(10L, request);

        assertEquals("Kaufland", response.title());     

        // Assert
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository).save(captor.capture());

        Transaction updated = captor.getValue();

        assertEquals("Kaufland", updated.getTitle());
        assertEquals("Weekly groceries", updated.getDescription());             
        assertEquals(new BigDecimal("200"), updated.getAmount());
        assertEquals(LocalDate.of(2026, 2, 10), updated.getDate());
        assertEquals(TransactionType.EXPENSE, updated.getType());
        assertEquals(category, updated.getCategory());

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

        verify(transactionRepository, never())
                .save(any(Transaction.class));
        }

    @Test
    void shouldReturnTransactionsPage() {

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

        Page<Transaction> page = new PageImpl<>(List.of(transaction));

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(transactionRepository.findAll(
                any(Specification.class), 
                any(Pageable.class)))
                .thenReturn(page);

        // Act
        Page<TransactionResponse> responsePage = transactionService.getAll(
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

        verify(transactionRepository).findAll(
                any(Specification.class), 
                any(Pageable.class));
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
                .findAll(any(Specification.class), any(Pageable.class));
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

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                CategoryNotFoundException.class,
                () -> transactionService.update(10L, request)
        );      

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

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "Salary",
                        "Google",
                        new BigDecimal("8000"),
                        LocalDate.now(),
                        TransactionType.INCOME,
                        1L
                );

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(categoryRepository.findById(1L))
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

        assertEquals(user, captor.getValue().getUser());
        }

    @Test
    void shouldAssignCategoryToTransactionWhenCreating() {

        // Arrange
        User user = new User();
        user.setId(1L);

        Category category = new Category();
        category.setId(7L);
        category.setName("Shopping");

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "Nike",
                        "Shoes",
                        new BigDecimal("450"),
                        LocalDate.now(),
                        TransactionType.EXPENSE,
                        7L
                );

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(categoryRepository.findById(7L))
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

        assertEquals(category, captor.getValue().getCategory());
        }

    @Test
    void shouldUseAscendingSort(){

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
                new TransactionFilter(null,null,null,null)
        );

        // Assert
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

        verify(transactionRepository)
                .findAll(any(Specification.class), captor.capture());

        Pageable pageable = captor.getValue();
        
        assertEquals(
                Sort.Direction.ASC,
                pageable.getSort().getOrderFor("amount").getDirection()
        );
    }

    @Test
    void shouldUseDescendingSort(){

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
                new TransactionFilter(null,null,null,null)
        );
        
        // Assert
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        
        verify(transactionRepository)
                .findAll(any(Specification.class), captor.capture());
        
        Pageable pageable = captor.getValue();
                
        assertEquals(
                Sort.Direction.DESC,
                pageable.getSort().getOrderFor("date").getDirection()
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
                new TransactionFilter(null, null, null, null)
        );

        // Assert
        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(transactionRepository)
                .findAll(any(Specification.class), captor.capture());

        Pageable pageable = captor.getValue();

        assertEquals(2, pageable.getPageNumber());
        assertEquals(5, pageable.getPageSize());
        }

}
