package com.ekahau.exercise.controller;

import com.ekahau.exercise.service.UserDetailsService;
import com.ekahau.exercise.user.User;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserDetailsService userDetailsService;

    public UserController(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostMapping(value = "/register/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    User registerUser(@RequestBody User user) {
        return userDetailsService.registerUser(user);
    }

    @GetMapping(value = "/find/user/{emailAddress}", produces = MediaType.APPLICATION_JSON_VALUE)
    User getUserDetails(@PathVariable("emailAddress") String emailAddress) {
        return userDetailsService.getUserDetailsByEmailAddress(emailAddress);
    }

    @PutMapping(value = "/update/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    User updateUser(@RequestBody User user) {
        return userDetailsService.updateUserDetails(user);
    }

    @DeleteMapping(value = "/delete/user/{emailAddress}", produces = MediaType.APPLICATION_JSON_VALUE)
    int deleteUser(@PathVariable("emailAddress") String emailAddress) {
        return userDetailsService.deleteUsersByEmailAddress(emailAddress);
    }
}
