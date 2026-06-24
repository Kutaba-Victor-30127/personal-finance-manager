package ro.kutaba.finance.category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

    List<Category> findByUserId(User user);
    
}
