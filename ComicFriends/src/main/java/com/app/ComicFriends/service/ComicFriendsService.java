package com.app.ComicFriends.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;

import com.app.ComicFriends.repositories.ComicPriceRepository;
import com.app.ComicFriends.repositories.ComicRepository;
import com.app.ComicFriends.repositories.UserRepository;
import com.app.ComicFriends.repositories.ReviewRepository;
import com.app.ComicFriends.repositories.TransactionRepository;
import com.app.ComicFriends.entities.Comic;
import com.app.ComicFriends.entities.ComicPrice;
import com.app.ComicFriends.entities.User;
import com.app.ComicFriends.entities.Review;
import com.app.ComicFriends.entities.Transaction;

@Service("comicservice")
public class ComicFriendsService {
    
	private final ComicRepository comicRepository;
	private final UserRepository userRepository;
	private final ReviewRepository reviewRepository;
	private final TransactionRepository transactionRepository;
	private final ComicPriceRepository comicPriceRepository;

	public ComicFriendsService(ComicRepository comicRepository, UserRepository userRepository, ReviewRepository reviewRepository, TransactionRepository transactionRepository, ComicPriceRepository comicPriceRepository) {
		this.comicRepository = comicRepository;
		this.userRepository = userRepository;
		this.reviewRepository = reviewRepository;
		this.transactionRepository = transactionRepository;
		this.comicPriceRepository = comicPriceRepository;
	}
	
	public List<Comic> findALLComics() {
		return (List<Comic>) comicRepository.findAll();
	}
	
	public List<ComicPrice> findAllComicPrice(Comic comic, User user) {
		return comicPriceRepository.findAllByComicAndUser(comic, user);
	}
	
	public List<Review> findALLReviewsByCreatorAndComic(User creator, Comic comic) {
		return (List<Review>) reviewRepository.findAllByCreatorAndReceiverComic(creator, comic);
	}
	
	public List<Review> findALLReviewsByCreatorAndUser(User creator, User user) {
		return (List<Review>) reviewRepository.findAllByCreatorAndReceiverUser(creator, user);
	}
	
	public Optional<Comic> findComicById(Long id) {
		return comicRepository.findById(id);
	}
	
	public Optional<ComicPrice> findComicPriceById(Long id) {
		return comicPriceRepository.findById(id);
	}

	public Long insert(Comic comic) {
		comic.setComicId(null);
		return comicRepository.save(comic).getComicId();
	}
	
	public Long insert(ComicPrice comicPrice) {
		comicPrice.setId(null);
		return comicPriceRepository.save(comicPrice).getId();
	}
	
	public List<User> findALLUser() {
		return (List<User>) userRepository.findAll();
	}
	
	public Optional<User> findUserById(Long id) {
		return userRepository.findById(id);
	}
	
	public Optional<User> findUserByName(String user) {
		return userRepository.findUserByUsername(user);
	}
	
	public Optional<User> findUserByEmail(String email) {
		return userRepository.findUserByEmail(email);
	}
	
	public Optional<Comic> findComic(int issue, String publisher, String title, int volume) {
		return comicRepository.findByIssueAndVolumeAndPublisherAndTitle(issue, publisher, title, volume);
	}
	
	public List<Review> findReviewByReceiver(Long receiverId) {
		
		Optional<User> opuserDB = findUserById(receiverId);
		User userDB = opuserDB.get();
		
		return (List<Review>) reviewRepository.findAllByReceiverUser(userDB);
	}
	
	public List<Review> findReviewByOwner(Long ownerId) {
		
		Optional<User> opuserDB = findUserById(ownerId);
		User userDB = opuserDB.get();
		
		return (List<Review>) reviewRepository.findAllByCreator(userDB);
	}
	
	public List<Review> findReviewByComic(Long comicId) {
		
		Optional<Comic> opcomicDB = findComicById(comicId);
		Comic comicDB = opcomicDB.get();
		
		return (List<Review>) reviewRepository.findAllByReceiverComic(comicDB);
	}

	public Long insert(User user) {
		user.setUserId(null);
		return userRepository.save(user).getUserId();
	}
	
	public Long update(User user) {
		
		Optional<User> opuserDB = findUserById(user.getUserId());
		User userDB = opuserDB.get();
		
		userDB.setUsername(user.getUsername());
		userDB.setCountry(user.getCountry());
		userDB.setDescription(user.getDescription());
		userDB.setEmail(user.getEmail());
		userDB.setName(user.getName());
		userDB.setPassword(user.getPassword());
		userDB.setSex(user.getSex());
		
		return userRepository.save(userDB).getUserId();
	}
	
	public Long update(Comic comic) {
		
		Optional<Comic> opcomicDB = findComicById(comic.getComicId());
		Comic comicDB = opcomicDB.get();
		
		comicDB.setAmount(comic.getAmount());
		comicDB.setCreators(comic.getCreators());
		comicDB.setIssue(comic.getIssue());
		comicDB.setPublisher(comic.getPublisher());
		comicDB.setTitle(comic.getTitle());
		comicDB.setVolume(comic.getVolume());
		
		return comicRepository.save(comicDB).getComicId();
	}
	
	public Long update(Review review) {
		
		Optional<Review> opreviewDB = findReviewById(review.getReviewId());
		Review reviewDB = opreviewDB.get();
		
		reviewDB.setComment(review.getComment());
		reviewDB.setCreator(review.getCreator());
		reviewDB.setDate(review.getDate());
		reviewDB.setReceiverComic(review.getReceiverComic());
		reviewDB.setReceiverUser(review.getReceiverUser());
		reviewDB.setScore(review.getScore());
		reviewDB.setTitle(review.getTitle());
		reviewDB.setType(review.getType());
		
		return reviewRepository.save(reviewDB).getReviewId();
	}
	
	public Long update(ComicPrice comicPrice) {
		
		Optional<ComicPrice> opcomicPriceDB = findComicPriceById(comicPrice.getId());
		ComicPrice comicPriceDB = opcomicPriceDB.get();
		
		comicPriceDB.setComic(comicPrice.getComic());
		comicPriceDB.setUser(comicPrice.getUser());
		comicPriceDB.setPrice(comicPrice.getPrice());
		
		return comicPriceRepository.save(comicPriceDB).getId();
	}
	
	public void delete (User user) {
		userRepository.delete(user);
	}
	
	public void delete (Review review) {
		reviewRepository.delete(review);
	}
	
	public void delete (Comic comic) {
		comicRepository.delete(comic);
	}
	
	public void delete (ComicPrice comicPrice) {
		comicPriceRepository.delete(comicPrice);
	}
	
	public List<Transaction> findALLTransaction() {
		return (List<Transaction>) transactionRepository.findAll();
	}
	
	public Optional<Transaction> findTransactionById(Long id) {
		return transactionRepository.findById(id);
	}

	public Long insert(Transaction transaction) {
		transaction.setTransactionId(null);
		return transactionRepository.save(transaction).getTransactionId();
	}
	
	public List<Review> findALLReview() {
		return (List<Review>) reviewRepository.findAll();
	}
	
	public Optional<Review> findReviewById(Long id) {
		return reviewRepository.findById(id);
	}

	public Long insert(Review review) {
		review.setReviewId(null);
		return reviewRepository.save(review).getReviewId();
	}
	
}