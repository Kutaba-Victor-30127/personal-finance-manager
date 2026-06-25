package ro.kutaba.finance.transaction;

import ro.kutaba.finance.category.Category;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import ro.kutaba.finance.user.User;
import ro.kutaba.finance.category.CategoryRepository;
import ro.kutaba.finance.user.UserRepository;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TransactionResponse create(CreateTransactionRequest request) {
        
        User user = getCurrentUser();

        Category category = getCategory(request.categoryId());

        Transaction transaction = new Transaction();
        transaction.setTitle(request.title());
        transaction.setDescription(request.description());
        transaction.setAmount(request.amount());
        transaction.setDate(request.date());
        transaction.setType(request.type());
        transaction.setCategory(category);
        transaction.setUser(user);

        Transaction saved = transactionRepository.save(transaction);
        
        return TransactionMapper.toResponse(saved);
    }  
    
    @Override
    public List<TransactionResponse> getAll() {
        User user = getCurrentUser();

        return transactionRepository.findByUser(user).stream()
                .map(TransactionMapper::toResponse)
                .toList();
    }

    @Override
    public TransactionResponse update(Long id, CreateTransactionRequest request) {

        User currentUser = getCurrentUser();

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        Category category = getCategory(request.categoryId());

        if (!transaction.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized to update this transaction");
        }

        transaction.setTitle(request.title());
        transaction.setDescription(request.description());
        transaction.setAmount(request.amount());
        transaction.setDate(request.date());
        transaction.setType(request.type());
        transaction.setCategory(category);

        Transaction updated = transactionRepository.save(transaction);
        
        return TransactionMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        User currentUser = getCurrentUser();

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized to delete this transaction");
        }

        transactionRepository.delete(transaction);
    }

    private User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Category getCategory(Long id){
        return categoryRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }
}
