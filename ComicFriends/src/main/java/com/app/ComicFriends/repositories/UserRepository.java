package com.app.ComicFriends.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.app.ComicFriends.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {
	
	Optional<User> findUserByUsername(String user);
	Optional<User> findUserByEmail(String email);
}