package com.projectbyPranayChavan.JournalApp.repository;

import com.projectbyPranayChavan.JournalApp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
       /* in our database we have field username so method must be findByUsername
          if you wrote findByUserName, findByuserName   then
          username field will mismatched and throws error */

    User findByUsername(String username);

    void deleteByUsername(String username);
}
