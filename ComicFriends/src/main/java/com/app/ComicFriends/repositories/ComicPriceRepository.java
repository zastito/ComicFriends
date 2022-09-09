package com.app.ComicFriends.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.app.ComicFriends.entities.Comic;
import com.app.ComicFriends.entities.ComicPrice;
import com.app.ComicFriends.entities.User;

public interface ComicPriceRepository extends CrudRepository<ComicPrice, Long> {
	List<ComicPrice> findAllByComicAndUser(Comic comic, User user);
}

