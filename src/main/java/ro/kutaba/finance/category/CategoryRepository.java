package ro.kutaba.finance.category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.kutaba.finance.user.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

    List<Category> findByUser(User user);

    Optional<Category> findByIdAndUser(Long id, User user);
    
}
