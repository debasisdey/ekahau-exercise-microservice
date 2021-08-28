package com.ekahau.exercise.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.ekahau.exercise.user.User;

/**
 * Spring data JPA will generate an implementation for this at runtime.
 */
@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
	@Transactional
	int deleteAllByEmailAddress(String emailAddress);

	User findByEmailAddress(String emailAddress);
}
