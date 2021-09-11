package com.app.ComicFriends.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.app.ComicFriends.entities.Comic;
import com.app.ComicFriends.entities.Review;
import com.app.ComicFriends.entities.User;

public interface ReviewRepository extends CrudRepository<Review, Long> {
	List<Review> findAllByReceiverUser(User receiver);
	List<Review> findAllByCreator(User creator);
	List<Review> findAllByReceiverComic(Comic comic);
	List<Review> findAllByCreatorAndReceiverUser(User creator, User receiver);
	List<Review> findAllByCreatorAndReceiverComic(User creator, Comic comic);
}