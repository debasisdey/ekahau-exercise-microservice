package com.ekahau.exercise.controller;

import com.ekahau.exercise.book.Book;
import com.ekahau.exercise.service.BookDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BookController {

    private final BookDetailsService bookDetailsService;

    @Autowired
    public BookController(BookDetailsService bookDetailsService) {
        this.bookDetailsService = bookDetailsService;
    }

    @PostMapping(value = "/register/book", consumes = MediaType.APPLICATION_JSON_VALUE)
    Book registerBook(@RequestBody Book book) {
        return bookDetailsService.registerBook(book);
    }

    @GetMapping(value = "/books", produces = MediaType.APPLICATION_JSON_VALUE)
    List<Book> getAllBooks() {
        return bookDetailsService.getAllBooks();
    }

    @GetMapping(value = "/book/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    Book getBooksByTitle(@PathVariable String title) {
        return bookDetailsService.getBookByTitle(title);
    }

    @DeleteMapping(value = "/delete/all/books")
    void deleteAllBooks() {
        bookDetailsService.deleteAllBooks();
    }

    @DeleteMapping(value = "/delete/book/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    int deleteBooksByTitle(@PathVariable String title) {
        return bookDetailsService.deleteBooksByTitle(title);
    }

    @PutMapping(value = "/update/book", produces = MediaType.APPLICATION_JSON_VALUE)
    Book updateBookByTitle(@RequestBody Book book) {
        return bookDetailsService.updateBooksByTitle(book);
    }

}
