package com.app.ComicFriends;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.app.ComicFriends.service.ComicFriendsService;

@SpringBootApplication
public class ComicFriendsApplication implements CommandLineRunner{

	@Autowired
	ComicFriendsService comicFriendsService;
	
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(ComicFriendsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
