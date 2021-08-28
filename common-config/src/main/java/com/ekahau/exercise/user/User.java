package com.ekahau.exercise.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Data
public class User {
	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="userid")
	private Integer userId;

	@Column(name="firstname")
	private String firstName;

	@Column(name="lastname")
	private String lastName;

	@NotBlank(message = "Email address can not be missing or empty")
	@Email
	@Column(name="emailaddress")
	private String emailAddress;

	@NotBlank(message = "password can not be missing or empty")
	private String password;

	public static User of(String firstName, String lastName, String emailAddress, String password) {
		User u = new User();
		u.firstName = firstName;
		u.lastName = lastName;
		u.emailAddress = emailAddress;
		u.password = password;
		return u;
	}
}
