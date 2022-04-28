package com.ekahau.exercise.service;

import com.ekahau.exercise.user.User;

public interface UserDetailsService {

    User registerUser(User user);

    User getUserDetailsByEmailAddress(String emailAddress);

    User updateUserDetails(User user);

    int deleteUsersByEmailAddress(String emailAddress);
}
