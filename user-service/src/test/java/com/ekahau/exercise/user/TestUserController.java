package com.ekahau.exercise.user;

import com.ekahau.exercise.mysql.server.MySqlServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TestUserController {

	@Autowired
	private TestRestTemplate template;

	@Autowired
	private UserRepository userRepository;

	@LocalServerPort
	int randomServerPort;

	private static final String LOCAL_BASE_URL = "http://localhost:";

	static MySqlServer mySqlServer = new MySqlServer();

	private static final String TEST_EMAIL_ADDRESS =  "test@test.com";
	private static final String TEST_PASSWORD =  "test";
	private static final String USER_DATA_SQL = "classpath:/testdata/user/testuserdata.sql";
	private static final String TRUNCATE_USER_SQL = "classpath:/testdata/user/truncateuser.sql";

	@BeforeAll
	static void runMysqlServer(){
		mySqlServer.start();
	}

	@Test
	@Sql(TRUNCATE_USER_SQL)
	public void shouldRegisterUserSuccessFully() throws URISyntaxException {
		User u = User.of("test", "test last name", TEST_EMAIL_ADDRESS, TEST_PASSWORD);
		final String baseUrl = LOCAL_BASE_URL + randomServerPort + "/api/v1/register/user";
		URI uri = new URI(baseUrl);
		HttpEntity<User> request = new HttpEntity<>(u, getHeader());
		ResponseEntity<User> result = this.template.postForEntity(uri, request, User.class);
		assertThat(result).isNotNull();
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result).extracting(HttpEntity::getBody).extracting(User::getLastName).isEqualTo("test last name");
	}

	@Test
	public void shouldReturnUnAuthorisedErrorWithOutCredential() {
		final String baseUrl = LOCAL_BASE_URL + randomServerPort + "/api/v1/find/user/"+TEST_EMAIL_ADDRESS;
		ResponseEntity<User> result = template.getForEntity(baseUrl, User.class);
		assertThat(result).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.UNAUTHORIZED);
	}


	@Test
	@Sql(USER_DATA_SQL)
	public void shouldReturnSuccessFullResponseWithCredential() {
		final String baseUrl = LOCAL_BASE_URL + randomServerPort + "/api/v1/find/user/"+TEST_EMAIL_ADDRESS;
		ResponseEntity<User> result = template.
				withBasicAuth(TEST_EMAIL_ADDRESS, TEST_PASSWORD)
				.getForEntity(baseUrl, User.class);
		assertThat(result).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
		assertThat(result).extracting(HttpEntity::getBody).extracting(User::getLastName).isEqualTo("testlastname1");
	}

	@Test
	@Sql(USER_DATA_SQL)
	public void shouldUpdateUserWithCredential() throws URISyntaxException {
		User u = User.of("test update", "test last name update", TEST_EMAIL_ADDRESS, TEST_PASSWORD);
		final String baseUrl = LOCAL_BASE_URL + randomServerPort + "/api/v1/update/user";
		URI uri = new URI(baseUrl);
		HttpEntity<User> request = new HttpEntity<>(u, getHeader());

		ResponseEntity<User> result = template
				.withBasicAuth(TEST_EMAIL_ADDRESS, TEST_PASSWORD)
				.exchange(uri, HttpMethod.PUT, request, User.class);

		assertThat(result).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);

		User updatedUser = userRepository.findByEmailAddress(TEST_EMAIL_ADDRESS);
		assertThat(updatedUser)
				.extracting(User::getLastName)
				.isEqualTo("test last name update");
		assertThat(updatedUser)
				.extracting(User::getFirstName)
				.isEqualTo("test update");
	}

	@Test
	@Sql(USER_DATA_SQL)
	public void shouldDeleteUserSuccessfullyWithCredential() throws URISyntaxException {
		final String baseUrl = LOCAL_BASE_URL + randomServerPort + "/api/v1/delete/user/"+TEST_EMAIL_ADDRESS;
		URI uri = new URI(baseUrl);
		ResponseEntity<Integer> result = template
				.withBasicAuth(TEST_EMAIL_ADDRESS, TEST_PASSWORD)
				.exchange(uri, HttpMethod.DELETE, new HttpEntity<>(getHeader()), Integer.class);
		assertThat(result).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);

		User updatedUser = userRepository.findByEmailAddress(TEST_EMAIL_ADDRESS);
		assertThat(updatedUser).isNull();
	}

	private HttpHeaders getHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		return headers;
	}

	@AfterAll
	static void cleanUp(){
		mySqlServer.stop();
	}
}
