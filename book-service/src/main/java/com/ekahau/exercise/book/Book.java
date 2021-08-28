package com.ekahau.exercise.book;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Entity
@Data
public class Book {
	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer bookId;

	@NotBlank(message = "Title can not be missing or empty")
	private String title;
	private String author;
	private String year;
	@NotBlank(message = "price can not be missing or empty")
	private String price;

	public static Book of(String title, String author, String year, String price) {
		Book b = new Book();
		b.title = title;
		b.author = author;
		b.year = year;
		b.price = price;
		return b;
	}
}

