package com.ekahau.exercise.service;

import com.ekahau.exercise.book.Book;

import java.util.List;

public interface BookDetailsService {

    Book registerBook(Book book);

    List<Book> getAllBooks();

    Book getBookByTitle(String title);

    void deleteAllBooks();

    int deleteBooksByTitle(String title);

    Book updateBooksByTitle(Book book);
}
