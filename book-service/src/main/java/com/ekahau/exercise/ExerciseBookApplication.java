package com.ekahau.exercise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExerciseBookApplication {

	public static void main(String[] args) {
		//MySqlServer mySqlServer = new MySqlServer();
		//mySqlServer.start();
		SpringApplication.run(ExerciseBookApplication.class, args);
	}

}
