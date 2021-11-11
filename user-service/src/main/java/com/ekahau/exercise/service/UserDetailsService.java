package com.ekahau.exercise.service;

import com.ekahau.exercise.user.User;
import com.ekahau.exercise.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserDetailsService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	public UserDetailsService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	public User registerUser(User user){
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	public User getUserDetailsByEmailAddress(String emailAddress){
		if(!emailAddress.equalsIgnoreCase(getUserNameFromAuthentication()))
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorised for other user");
		User user = userRepository.findByEmailAddress(emailAddress);
		if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No User found with the Email Address");
		return user;
	}

	public User updateUserDetails(User user){
		if(!user.getEmailAddress().equalsIgnoreCase(getUserNameFromAuthentication()))
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorised for other user");
		User oldUser = userRepository.findByEmailAddress(user.getEmailAddress());
		if (oldUser == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No User found with the Email Address");
		user.setUserId(oldUser.getUserId());
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	public int deleteUsersByEmailAddress(String emailAddress){
		if(!emailAddress.equalsIgnoreCase(getUserNameFromAuthentication()))
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorised for other user");
		return userRepository.deleteAllByEmailAddress(emailAddress);
	}

	private String getUserNameFromAuthentication(){
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

}
