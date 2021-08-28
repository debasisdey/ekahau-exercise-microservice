package com.ekahau.exercise.book;

import com.ekahau.exercise.mysql.server.MySqlServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TestBookRepository {
	@Autowired
	BookRepository bookRepository;

	static MySqlServer mySqlServer = new MySqlServer();

	@BeforeAll
	static void runMysqlServer(){
		mySqlServer.start();
	}

	@Test
	public void shouldSaveBookSuccessFully(){
		Book book = new Book();
		book.setTitle("My Test Book");
		book.setAuthor("Test User");
		book.setPrice("100.00£");
		book.setYear("2005");
		bookRepository.save(book);
		assertThat(bookRepository.findByTitle("My Test Book")).isNotNull();
	}

	@Test
	@Sql("classpath:/testdata/book/bookdata.sql")
	public void shouldSaveBooksThroughSqlFileAndReurnAllAndByTitle() {
		Book book1 = bookRepository.findByTitle("My_test_title1");
		Book book2 = bookRepository.findByTitle("My_test_title2");
		Book book3 = bookRepository.findByTitle("My_test_title3");
		List<Book> bookList = bookRepository.findAll();
		assertThat(book1).extracting(Book::getAuthor).isEqualTo("Mu user test 1");
		assertThat(book2).extracting(Book::getPrice).isEqualTo("200.00£");
		assertThat(book3).extracting(Book::getYear).isEqualTo("2010");
		assertThat(bookList).hasSize(3);
	}

	@Test
	@Sql("classpath:/testdata/book/bookdata.sql")
	public void shouldUpdateBookAndReturnUpdatedBook(){
		Book oldBook = bookRepository.findByTitle("My_test_title1");
		oldBook.setPrice("1000.00£");
		bookRepository.save(oldBook);
		Book afterUpdateBook = bookRepository.findByTitle("My_test_title1");
		assertThat(afterUpdateBook).extracting(Book::getPrice).isEqualTo("1000.00£");
	}

	@Test
	@Sql("classpath:/testdata/book/bookdata.sql")
	public void shouldDeleteBookSuccessFully(){
		bookRepository.deleteByTitle("My test title1");
		Book afterDeleteBook = bookRepository.findByTitle("My test title1");
		assertThat(afterDeleteBook).isNull();
	}

	@Test
	@Sql("classpath:/testdata/book/bookdata.sql")
	public void shouldDeleteAllBooksSuccessFully(){
		bookRepository.deleteAll();
		List<Book> bookList = bookRepository.findAll();
		assertThat(bookList).isEmpty();
	}

	@AfterAll
	static void cleanUp(){
		mySqlServer.stop();
	}

}
