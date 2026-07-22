package com.projectbyPranayChavan.JournalApp.service;

import com.projectbyPranayChavan.JournalApp.entities.JournalEntry;
import com.projectbyPranayChavan.JournalApp.entities.User;
import com.projectbyPranayChavan.JournalApp.repository.JournalEntryRepository;
import com.projectbyPranayChavan.JournalApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.util.*;

@Slf4j  // its a A Lombok annotation that automatically creates an SLF4J Logger named log for the class.
@Service
public class UserService {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Autowired
    private UserRepository userRepository;

    public boolean saveNewUser(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Arrays.asList("USER"));
            userRepository.save(user);
            return true;

        } catch (Exception e) {
            log.error("hahahhahhahahahah");
            log.warn("hahahhahhahahahah");
            log.info("hahahhahhahahahah");
            log.debug("hahahhahhahahahah");
            log.trace("hahahhahhahahahah");
            return false;

        }

    }

    // save user from browser to database
    public void saveUser(User user) {
        /* Generate unique id and time because user not gonna provide this two fields so
        create this two fields for user at service layer */
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        userRepository.save(user);

    }

    // to save admin
    public void saveAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER", "ADMIN"));
        userRepository.save(user);
    }

    // retrieve all data
    public List<User> getAll() {
        return userRepository.findAll();
    }

    // retrieve data using id
    //Optional either return or null
    public Optional<User> getById(UUID id) {
        return userRepository.findById(id);
    }

    // delete entry
    public void deleteById(UUID id) {
        userRepository.deleteById(id);

    }



    // find user using username
    public User findByUserName(String username) {
        return userRepository.findByUsername(username);
    }
}
