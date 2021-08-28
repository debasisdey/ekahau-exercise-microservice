package com.ekahau.exercise.controller;

import com.ekahau.exercise.user.User;
import com.ekahau.exercise.user.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
		return userRepository.findByEmailAddress(emailAddress);
	}

	@PutMapping(value = "/update/user", consumes = MediaType.APPLICATION_JSON_VALUE)
	User updateUser(@RequestBody User user){
		User oldUser = userRepository.findByEmailAddress(user.getEmailAddress());
		user.setUserId(oldUser.getUserId());
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	@DeleteMapping(value = "/delete/user/{emailAddress}", produces = MediaType.APPLICATION_JSON_VALUE)
	int deleteUser(@PathVariable("emailAddress") String emailAddress){
		return userRepository.deleteAllByEmailAddress(emailAddress);
	}

}
