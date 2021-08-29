package com.ekahau.exercise.controller;

import com.ekahau.exercise.user.User;
import com.ekahau.exercise.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1")
public class UserController {

	private final UserRepository userRepository;

	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public UserController(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@PostMapping(value = "/register/user", consumes = MediaType.APPLICATION_JSON_VALUE)
	User registerUser(@RequestBody User user){
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	@GetMapping(value="/find/user/{emailAddress}", produces = MediaType.APPLICATION_JSON_VALUE)
	User getUserDetails(@PathVariable("emailAddress") String emailAddress){
		if(!emailAddress.equalsIgnoreCase(getUserNameFromAuthentication()))
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorised for other user");
		User user = userRepository.findByEmailAddress(emailAddress);
		if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No User found with the Email Address");
		return user;
	}

	@PutMapping(value = "/update/user", consumes = MediaType.APPLICATION_JSON_VALUE)
	User updateUser(@RequestBody User user) {
		if(!user.getEmailAddress().equalsIgnoreCase(getUserNameFromAuthentication()))
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorised for other user");
		User oldUser = userRepository.findByEmailAddress(user.getEmailAddress());
		if (oldUser == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No User found with the Email Address");
		user.setUserId(oldUser.getUserId());
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	@DeleteMapping(value = "/delete/user/{emailAddress}", produces = MediaType.APPLICATION_JSON_VALUE)
	int deleteUser(@PathVariable("emailAddress") String emailAddress){
		if(!emailAddress.equalsIgnoreCase(getUserNameFromAuthentication()))
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorised for other user");
		return userRepository.deleteAllByEmailAddress(emailAddress);
	}

	private String getUserNameFromAuthentication(){
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

}
