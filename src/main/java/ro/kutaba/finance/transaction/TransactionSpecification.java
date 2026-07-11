package ro.kutaba.finance.transaction;

import ro.kutaba.finance.user.User;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {
    
    public static Specification<Transaction> withFilter(TransactionFilter filter, User currentUser) {

        return (root, query, criteriaBuilder) -> {
            
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                criteriaBuilder.equal(
                    root.get("user").get("id"),
                    currentUser.getId()
                )
            );

            if (filter.type() != null) {

                predicates.add(
                                criteriaBuilder.equal(
                                    root.get("type"),
                                    filter.type()
                                )
                            );
            }

            if (filter.categoryId() != null) {

                predicates.add(
                                criteriaBuilder.equal(
                                    root.get("category").get("id"),
                                    filter.categoryId()
                                )
                            );
            }

            if (filter.startDate() != null) {

                predicates.add(
                                criteriaBuilder.greaterThanOrEqualTo(
                                    root.get("date"),
                                    filter.startDate()
                                )
                            );
            }

            if (filter.endDate() != null) {

                predicates.add(
                                criteriaBuilder.lessThanOrEqualTo(
                                    root.get("date"),
                                    filter.endDate()
                                )
                            );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
