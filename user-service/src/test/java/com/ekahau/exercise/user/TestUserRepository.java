package com.ekahau.exercise.user;

import com.ekahau.exercise.mysql.server.MySqlServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TestUserRepository {
	@Autowired
	UserRepository userRepository;

	static MySqlServer mySqlServer = new MySqlServer();

	@BeforeAll
	static void runMysqlServer(){
		mySqlServer.start();
	}

	@Test
	public void shouldSaveUserSuccessFully(){
		User u = new User();
		u.setFirstName("test");
		u.setLastName("test last name");
		u.setEmailAddress("test@test.com");
		u.setPassword("test");
		userRepository.save(u);
		assertThat(userRepository.findByEmailAddress("test@test.com")).isNotNull();
	}

	@Test
	@Sql("classpath:/testdata/user/userdata.sql")
	public void shouldSaveUsersThroughSqlFileAndReurnByEmailAddress() {
		User user1 = userRepository.findByEmailAddress("123@test.com");
		User user2 = userRepository.findByEmailAddress("124@test.com");
		User user3 = userRepository.findByEmailAddress("125@test.com");
		assertThat(user1).extracting(User::getFirstName).isEqualTo("test1");
		assertThat(user2).extracting(User::getLastName).isEqualTo("testlastname2");
		assertThat(user3).extracting(User::getPassword).isEqualTo("test123456");
	}

	@Test
	@Sql("classpath:/testdata/user/userdata.sql")
	public void shouldUpdateUserAndReturnUpdatedUser(){
		User oldUser = userRepository.findByEmailAddress("123@test.com");
		oldUser.setPassword("MyNewPassword");
		userRepository.save(oldUser);
		User afterUpdateUser = userRepository.findByEmailAddress("123@test.com");
		assertThat(afterUpdateUser).extracting(User::getPassword).isEqualTo("MyNewPassword");
	}

	@Test
	@Sql("classpath:/testdata/user/userdata.sql")
	public void shouldDeleteUserSuccessFully(){
		userRepository.deleteAllByEmailAddress("123@test.com");
		User afterDeleteUser = userRepository.findByEmailAddress("123@test.com");
		assertThat(afterDeleteUser).isNull();
	}

	@AfterAll
	static void cleanUp(){
		mySqlServer.stop();
	}

}
