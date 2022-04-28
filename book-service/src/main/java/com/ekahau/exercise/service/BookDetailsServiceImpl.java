package com.ekahau.exercise.service;

import com.ekahau.exercise.book.Book;
import com.ekahau.exercise.book.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BookDetailsServiceImpl implements BookDetailsService {

    private final BookRepository bookRepository;

    @Autowired
    public BookDetailsServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book registerBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book getBookByTitle(String title) {
        Book book = bookRepository.findByTitle(title);
        if (book == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Book found with this title");
        return book;
    }

    @Override
    public void deleteAllBooks() {
        bookRepository.deleteAll();
    }

    @Override
    public int deleteBooksByTitle(String title) {
        return bookRepository.deleteByTitle(title);
    }

    @Override
    public Book updateBooksByTitle(Book book) {
        Book oldBook = bookRepository.findByTitle(book.getTitle());
        if (oldBook == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Book found with this title");
        book.setBookId(oldBook.getBookId());
        return bookRepository.save(book);
    }
}
