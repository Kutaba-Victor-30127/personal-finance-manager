package ro.kutaba.finance.transaction;

import java.util.List;
import ro.kutaba.finance.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByCategoryId(Long categoryId);

    List<Transaction> findByType(TransactionType type);

    List<Transaction> findByUser(User user);
}