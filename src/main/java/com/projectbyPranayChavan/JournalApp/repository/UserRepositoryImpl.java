package com.projectbyPranayChavan.JournalApp.repository;



import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import com.projectbyPranayChavan.JournalApp.entities.User;
import org.springframework.stereotype.Repository;

import java.util.List;

/* why we need to create this class??
    suppose we want users only basis of their email address is not null and
    they have true value of sentimentAnalysis using methods of JpaRepository its
    very difficuilt to achieve or we cant achieve so we are using Criteria for retrieve data
    or to perform CRUD operation by using query with custom modification
 */
public class UserRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;
   // This method will be useful for users with email and who provided sentiment details
    public List<User> getUserForSA() {

        // Get CriteriaBuilder to build the query
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Create a query that will return User objects
        CriteriaQuery<User> query = cb.createQuery(User.class);

        // Specify that we are querying the User entity
        Root<User> root = query.from(User.class);

        // Condition 1: sentimentAnalysis must be true
        Predicate sentimentCondition =
                cb.isTrue(root.get("sentimentAnalysis"));

        // Condition 2: email should not be null
        Predicate emailNotNull =
                cb.isNotNull(root.get("email"));

        // Apply both conditions
        query.select(root)
                .where(
                        cb.and(
                                emailNotNull,
                                sentimentCondition
                        )
                );

        // Execute query and return users
        return entityManager
                .createQuery(query)
                .getResultList();
    }
}
