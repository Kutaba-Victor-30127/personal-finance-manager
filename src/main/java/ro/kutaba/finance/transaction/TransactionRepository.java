package ro.kutaba.finance.transaction;

import java.util.List;
import ro.kutaba.finance.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionRepository extends JpaRepository<Transaction, Long>,
                                                JpaSpecificationExecutor<Transaction> {
                                                    
    List<Transaction> findByUser(User user);

    Page<Transaction> findByUser(User user, Pageable pageable);
}