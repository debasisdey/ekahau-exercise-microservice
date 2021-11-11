package com.ekahau.exercise.service;

import com.ekahau.exercise.book.Book;
import com.ekahau.exercise.book.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BookDetailsService {

	private final BookRepository bookRepository;

	@Autowired
	public BookDetailsService(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	public Book registerBook(Book book){
		return bookRepository.save(book);
	}

	public List<Book> getAllBooks(){
		return bookRepository.findAll();
	}

	public Book getBookByTitle(String title){
		Book book = bookRepository.findByTitle(title);
		if (book == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Book found with this title");
		return book;
	}

	public void deleteAllBooks(){
		bookRepository.deleteAll();
	}

	public int deleteBooksByTitle(String title){
		return bookRepository.deleteByTitle(title);
	}

	public Book updateBooksByTitle(Book book){
		Book oldBook = bookRepository.findByTitle(book.getTitle());
		if (oldBook == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Book found with this title");
		book.setBookId(oldBook.getBookId());
		return bookRepository.save(book);
	}
}
