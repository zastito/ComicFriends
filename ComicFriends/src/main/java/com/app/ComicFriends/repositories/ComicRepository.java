package com.app.ComicFriends.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.app.ComicFriends.entities.Comic;

public interface ComicRepository extends CrudRepository<Comic, Long> {
	
	@Query("SELECT c FROM Comic c WHERE (:issue = -1 or c.issue = :issue) and (:publisher LIKE '' or c.publisher = :publisher) and (:title LIKE '' or c.title = :title) and (:volume = -1 or c.volume = :volume)")
	Optional<Comic> findByIssueAndVolumeAndPublisherAndTitle(int issue, String publisher, String title, int volume);	
}