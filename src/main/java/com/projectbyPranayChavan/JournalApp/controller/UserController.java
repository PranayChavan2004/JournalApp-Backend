package com.projectbyPranayChavan.JournalApp.controller;

import com.projectbyPranayChavan.JournalApp.api.response.WeatherResponse;
import com.projectbyPranayChavan.JournalApp.entities.User;
import com.projectbyPranayChavan.JournalApp.repository.UserRepository;
import com.projectbyPranayChavan.JournalApp.service.UserService;
import com.projectbyPranayChavan.JournalApp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    // Injecting UserService to access business logic
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeatherService weatherService;


    // ==========================================================
    // Get All Users
    // GET : /user
    // ==========================================================
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {

        List<User> users = userService.getAll();

        return ResponseEntity.ok(users);
    }

    // ==========================================================
    // Get User By Id
    // GET : /user/{id}
    // ==========================================================
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {

        User user = userService.getById(id).orElse(null);

        if (user != null) {
            return ResponseEntity.ok(user);
        }

        return ResponseEntity.notFound().build();
    }

    // ==========================================================
    // Find User By Username
    // GET : /user/username/{username}
    // ==========================================================
    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {

        User user = userService.findByUserName(username);

        if (user != null) {
            return ResponseEntity.ok(user);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // ==========================================================
    // Update User using id and username
    // PUT : /user/{id}
    // ==========================================================
    @PutMapping
    public ResponseEntity<String> updateUser(@RequestBody User newUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        User oldUser = userService.findByUserName(userName);

        if (oldUser != null) {

            oldUser.setUsername(
                    newUser.getUsername() != null &&
                            !newUser.getUsername().isBlank()
                            ? newUser.getUsername()
                            : oldUser.getUsername()
            );

            oldUser.setPassword(
                    newUser.getPassword() != null &&
                            !newUser.getPassword().isBlank()
                            ? newUser.getPassword()
                            : oldUser.getPassword()
            );

            // Save updated user
            userService.saveUser(oldUser); //--> we are using saveUser because dont want to encode password again

            return ResponseEntity.ok("User Updated Successfully");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("User Not Found");
    }



    // ==========================================================
    // Delete User
    // DELETE : /user/{id}
    // ==========================================================
    @DeleteMapping
    public ResponseEntity<String> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userRepository.deleteByUsername(authentication.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/weather")
    public ResponseEntity<?> greeting() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        WeatherResponse weatherResponse = weatherService.getWeather("Mumbai");
        String greeting = "";
        if (weatherResponse != null) {
            greeting = ", Weather feels like " + weatherResponse.getCurrent().getFeelslike();
        }
        return new ResponseEntity<>("Hi " + authentication.getName() + greeting, HttpStatus.OK);
    }

}