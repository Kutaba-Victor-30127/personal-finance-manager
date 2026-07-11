package ro.kutaba.finance.transaction;

import ro.kutaba.finance.category.Category;

import org.springframework.stereotype.Service;
import ro.kutaba.finance.user.User;
import ro.kutaba.finance.category.CategoryRepository;
import ro.kutaba.finance.exception.CategoryNotFoundException;
import ro.kutaba.finance.exception.TransactionNotFoundException;
import ro.kutaba.finance.exception.UnauthorizedException;
import ro.kutaba.finance.security.CurrentUserService;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final CurrentUserService currentUserService;

    public TransactionServiceImpl(TransactionRepository transactionRepository, CategoryRepository categoryRepository, CurrentUserService currentUserService) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public TransactionResponse create(CreateTransactionRequest request) {
        
        User user = currentUserService.getCurrentUser();

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
    public Page<TransactionResponse> getAll(
                        int page, 
                        int size,
                        String sortBy,
                        String sortDir,
                        TransactionFilter filter) {

        User user = currentUserService.getCurrentUser();

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() 
                                                    : Sort.by(sortBy).descending();
                    
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Transaction> transactionsPage =
                    transactionRepository.findAll(
                            TransactionSpecification.withFilter(filter, user), 
                            pageable);

        return transactionsPage.map(TransactionMapper::toResponse);
    }
    

    @Override
    public TransactionResponse update(Long id, CreateTransactionRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(TransactionNotFoundException::new);

        validateOwner(transaction, currentUser);

        Category category = getCategory(request.categoryId());

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
        User currentUser = currentUserService.getCurrentUser();

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(TransactionNotFoundException::new);
        
        validateOwner(transaction, currentUser);
        
        transactionRepository.delete(transaction);
    }

    private Category getCategory(Long id){
        return categoryRepository
                .findById(id)
                .orElseThrow(CategoryNotFoundException::new);
    }

    private void validateOwner(Transaction transaction, User currentUser){
        
        if (!transaction.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException();
        }
    }
}
