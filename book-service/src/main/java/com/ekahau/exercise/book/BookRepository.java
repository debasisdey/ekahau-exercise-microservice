package com.ekahau.exercise.book;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring data JPA will generate an implementation for this at runtime.
 */
@Repository
public interface BookRepository extends CrudRepository<Book, Integer> {
	Book findByTitle(String title);
	List<Book> findAll();

	@Transactional
	int deleteByTitle(String title);
}