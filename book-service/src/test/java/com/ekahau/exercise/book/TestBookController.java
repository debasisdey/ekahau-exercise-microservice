package com.ekahau.exercise.book;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TestBookController {

	@Autowired
	private TestRestTemplate template;

	@Autowired
	private BookRepository bookRepository;

	@LocalServerPort
	int randomServerPort;

	private static final String LOCAL_BASE_URL = "http://localhost:";

	static MySqlServer mySqlServer = new MySqlServer();

	private static final String TEST_EMAIL_ADDRESS =  "test@test.com";
	private static final String TEST_PASSWORD =  "test";
	private static final String BOOK_TITLE =  "Test_Book";
	private static final String BOOK_TITLE_1 = "My_test_title1";
	private static final String USER_DATA_SQL = "classpath:/testdata/user/testuserdata.sql";
	private static final String BOOK_DATA_SQL = "classpath:/testdata/book/bookdata.sql";
	private static final String TRUNCATE_USER_SQL = "classpath:/testdata/book/truncateuser.sql";

	@BeforeAll
	static void runMysqlServer(){
		mySqlServer.start();
	}

	@Test
	@Sql(USER_DATA_SQL)
	public void shouldRegisterBookSuccessFully() throws URISyntaxException {
		Book book = Book.of(BOOK_TITLE, "Test Author", "2000", "200.00£");
		URI uri = new URI(LOCAL_BASE_URL + randomServerPort + "/api/v1/register/book");
		HttpEntity<Book> request = new HttpEntity<>(book, getHeader());
		ResponseEntity<Book> result = template
				.withBasicAuth(TEST_EMAIL_ADDRESS, TEST_PASSWORD)
				.postForEntity(uri, request, Book.class);
		assertThat(result).isNotNull();
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

		Book registeredBook = bookRepository.findByTitle(BOOK_TITLE);
		assertThat(registeredBook).extracting(Book::getAuthor).isEqualTo("Test Author");
	}

	@Test
	@Sql(USER_DATA_SQL)
	public void shouldBeUnAuthorisedIfUnAuthenticatedRequestSent() throws URISyntaxException {
		Book book = Book.of("Test_Book", "Test Author", "2000", "200.00£");
		URI uri = new URI(LOCAL_BASE_URL + randomServerPort + "/api/v1/register/book");
		HttpEntity<Book> request = new HttpEntity<>(book, getHeader());
		ResponseEntity<Book> result = template
				.withBasicAuth(TEST_EMAIL_ADDRESS, "TEST_PASSWORD")
				.postForEntity(uri, request, Book.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	@Sql(TRUNCATE_USER_SQL)
	public void shouldBeUnAuthorisedIfUserDoesNotExist() throws URISyntaxException {
		Book book = Book.of("Test Book", "Test Author", "2000", "200.00£");
		URI uri = new URI(LOCAL_BASE_URL + randomServerPort + "/api/v1/register/book");
		HttpEntity<Book> request = new HttpEntity<>(book, getHeader());
		ResponseEntity<Book> result = template
				.withBasicAuth(TEST_EMAIL_ADDRESS, TEST_PASSWORD)
				.postForEntity(uri, request, Book.class);
		assertThat(result).isNotNull();
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	@Sql(TRUNCATE_USER_SQL)
	public void getBooksApiIsUnauthorisedWithoutUserAuthentication() {
		final String baseUrl = LOCAL_BASE_URL + randomServerPort + "/api/v1/books";
		ResponseEntity<Book> result = template
				.withBasicAuth(TEST_EMAIL_ADDRESS, TEST_PASSWORD)
				.getForEntity(baseUrl, Book.class);
		assertThat(result).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	@Sql(value = {USER_DATA_SQL , BOOK_DATA_SQL})
	public void getBooksApiWillBeSuccessWithUserAuthentication() {
		final String baseUrl = LOCAL_BASE_URL + randomServerPort + "/api/v1/books";
		ResponseEntity<List> result = template
				.withBasicAuth(TEST_EMAIL_ADDRESS, TEST_PASSWORD)
				.getForEntity(baseUrl, List.class);
		assertThat(result).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);

		List<Book> bookList = bookRepository.findAll();
		assertThat(bookList).hasSize(3);
	}

	@Test
	@Sql(TRUNCATE_USER_SQL)
	public void getBooksByTitleApiIsUnauthorisedWithoutUserAuthentication() {
		final String baseUrl = LOCAL_BASE_URL + randomServerPort + "/api/v1/book/" + BOOK_TITLE;
		ResponseEntity<Book> result = template
				.withBasicAuth(TEST_EMAIL_ADDRESS, TEST_PASSWORD)
				.getForEntity(baseUrl, Book.class);
		assertThat(result).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	@Sql(value = {USER_DATA_SQL , BOOK_DATA_SQL})
	public void getBooksByTitleWillBeSuccessWithUserAuthentication() {
		final String baseUrl = LOCAL_BASE_URL + randomServerPort + "/api/v1/book/" + BOOK_TITLE_1;
		ResponseEntity<Book> result = template
				.withBasicAuth(TEST_EMAIL_ADDRESS, TEST_PASSWORD)
				.getForEntity(baseUrl, Book.class);
		assertThat(result).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);

		Book book = bookRepository.findByTitle(BOOK_TITLE_1);
		assertThat(book).extracting(Book::getPrice).isEqualTo("100.00£");
	}

	@Test
	@Sql(TRUNCATE_USER_SQL)
	public void deleteAllBooksApiIsUnauthorisedWithoutUserAuthentication() throws URISyntaxException {
		final String baseUrl = LOCAL_BASE_URL + randomServerPort + "/api/v1/delete/all/books";
		URI uri = new URI(baseUrl);
		ResponseEntity<Integer> result = (ResponseEntity<Integer>) callToRestApi(uri, HttpMethod.DELETE, new HttpEntity<>(getHeader()), Integer.class);
		assertThat(result).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	@Sql(value = {USER_DATA_SQL , BOOK_DATA_SQL})
	public void deleteAllBooksApiWillBeSuccessWithUserAuthentication() throws URISyntaxException {
		URI uri = new URI(LOCAL_BASE_URL + randomServerPort + "/api/v1/delete/all/books");
		ResponseEntity<Integer> result = (ResponseEntity<Integer>) callToRestApi(uri, HttpMethod.DELETE, new HttpEntity<>(getHeader()), Integer.class);
		assertThat(result).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);

		List<Book> bookList = bookRepository.findAll();
		assertThat(bookList).isEmpty();
	}

	@Test
	@Sql(TRUNCATE_USER_SQL)
	public void deleteBooksByTitleApiIsUnauthorisedWithoutUserAuthentication() throws URISyntaxException {
		URI uri = new URI(LOCAL_BASE_URL + randomServerPort + "/api/v1/delete/book/" + BOOK_TITLE);
		ResponseEntity<Integer> result = (ResponseEntity<Integer>) callToRestApi(uri, HttpMethod.DELETE, new HttpEntity<>(getHeader()), Integer.class);
		assertThat(result).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	@Sql(value = {USER_DATA_SQL , BOOK_DATA_SQL})
	public void deleteBooksByTitleApiWillBeSuccessWithUserAuthentication() throws URISyntaxException {
		URI uri = new URI(LOCAL_BASE_URL + randomServerPort + "/api/v1/delete/book/" + BOOK_TITLE_1);
		ResponseEntity<Integer> result = (ResponseEntity<Integer>) callToRestApi(uri, HttpMethod.DELETE,
				new HttpEntity<>(getHeader()), Integer.class);
		assertThat(result).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);

		List<Book> bookList = bookRepository.findAll();
		assertThat(bookList).hasSize(2);
	}

	@Test
	@Sql(TRUNCATE_USER_SQL)
	public void updateBooksApiIsUnauthorisedWithoutUserAuthentication() throws URISyntaxException {
		Book book = Book.of("Test Book", "Test Author", "2000", "200.00£");
		URI uri = new URI(LOCAL_BASE_URL + randomServerPort + "/api/v1/update/book");
		HttpEntity<Book> request = new HttpEntity<>(book, getHeader());

		ResponseEntity<Book> result = (ResponseEntity<Book>) callToRestApi(uri, HttpMethod.PUT, request, Book.class);
		assertThat(result).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	@Sql(value = {USER_DATA_SQL , BOOK_DATA_SQL})
	public void updateBooksApiIsSuccessfulWithUserAuthentication() throws URISyntaxException {
		Book book = Book.of(BOOK_TITLE_1, "Test Author", "2000", "200.00£");
		URI uri = new URI(LOCAL_BASE_URL + randomServerPort + "/api/v1/update/book");
		HttpEntity<Book> request = new HttpEntity<>(book, getHeader());

		ResponseEntity<Book> result = (ResponseEntity<Book>) callToRestApi(uri, HttpMethod.PUT, request, Book.class);
		assertThat(result).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);

		Book updatedBook = bookRepository.findByTitle(BOOK_TITLE_1);
		book.setBookId(updatedBook.getBookId());
		assertThat(updatedBook).isEqualTo(book);
	}

	private ResponseEntity<?> callToRestApi(URI uri, HttpMethod method, HttpEntity<?> request, Class<?> responseType){
		return template
				.withBasicAuth(TEST_EMAIL_ADDRESS, TEST_PASSWORD)
				.exchange(uri, method, request, responseType);
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
