package com.app.ComicFriends.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.app.ComicFriends.entities.Comic;
import com.app.ComicFriends.entities.ComicPrice;
import com.app.ComicFriends.entities.Review;
import com.app.ComicFriends.entities.ReviewType;
import com.app.ComicFriends.entities.Transaction;
import com.app.ComicFriends.entities.User;
import com.app.ComicFriends.exception.ComicNotFoundException;
import com.app.ComicFriends.exception.ReviewImputDataException;
import com.app.ComicFriends.exception.ReviewNotFoundException;
import com.app.ComicFriends.exception.UserImputDataException;
import com.app.ComicFriends.exception.UserNotFoundException;
import com.app.ComicFriends.service.ComicFriendsService;

@RestController
@RequestMapping("/ComicFriends")
public class ComicVineController {
	
    @Value("${spring.social.facebook.appId}")
    String facebookAppId;
    @Value("${spring.social.facebook.appSecret}")
    String facebookSecret;
    String accessToken;
	@Value("${spring.ComicVine.api_key}")
    String api_key;
	
	@Autowired
	private RestTemplate restTemplate;
	@Resource(name = "comicservice")
	private ComicFriendsService comicservice;
	
	@PostMapping("/user/add")
	public Long addUser(@Valid @RequestBody User user) throws UserImputDataException {
		Optional<User> user1 = comicservice.findUserByEmail(user.getEmail());
		Optional<User> user2 = comicservice.findUserByName((user.getUsername()));
		
		if (user.getUsername().isEmpty() || user.getPassword().isEmpty() || !user.getEmail().matches(".*@.*")) {
			throw new UserImputDataException("Por favor, revisa que todos los datos introducidos sean correctos");
		}
		
		if (!user1.isEmpty()) {
			throw new UserImputDataException("Email ya existente. Por favor, seleccione otro");
		}

		if (!user2.isEmpty()) {
			throw new UserImputDataException("Username ya existente. Por favor, seleccione otro");
		}
		
		return comicservice.insert(user);
	}
	
	@GetMapping("/user/{id}")
	public User getUserById(@PathVariable(value = "id") Long userId) throws UserNotFoundException {
		Optional<User> user = comicservice.findUserById(userId);
		
		try {
			user.get().getUserId();
		}
		catch(Exception e) {
			throw new UserNotFoundException("Usuario no encontrado");
		}
		
		return user.get();
	}
	
	@PostMapping("/user/modify")
	public Long modifyUser(@Valid @RequestBody User user) throws UserImputDataException {
		Optional<User> user1 = comicservice.findUserById(user.getUserId());
		Optional<User> user2 = comicservice.findUserByEmail(user.getEmail());
		Optional<User> user3 = comicservice.findUserByName((user.getUsername()));
		
		if (user.getUsername().isEmpty() || user.getPassword().isEmpty() || !user.getEmail().matches(".*@.*")) {
			throw new UserImputDataException("Por favor, revisa que todos los datos introducidos sean correctos");
		}
		
		if (!user1.isEmpty() && !user2.isEmpty()) {
			if (user2.get().getUserId() != user.getUserId()) {
				throw new UserImputDataException("Email ya existente. Por favor, seleccione otro");
			}		
		}

		if (!user1.isEmpty() && !user3.isEmpty()) {
			if (user3.get().getUserId() != user.getUserId()) {
				throw new UserImputDataException("Username ya existente. Por favor, seleccione otro");
			}
		}
		
		return comicservice.update(user);
	}
	
	@PostMapping("/user/delete")
	public void deleteUser(@Valid @RequestBody User user) throws UserNotFoundException {
		List<Review> reviews = comicservice.findReviewByReceiver(user.getUserId());
		List<Review> reviews2 = comicservice.findReviewByOwner(user.getUserId());
		
		List<Comic> comics = comicservice.findALLComics();
		User userbd = getUserById(user.getUserId());
		
		for (Comic comic: comics) {
			boolean b = comic.getOwners().contains(userbd);
			if (b) {
				deleteComic(comic, user.getUserId());
			}
		}		
		
		for (Review review: reviews)
			comicservice.delete(review);
		
		for (Review review: reviews2)
			comicservice.delete(review);
		
		comicservice.delete(user);
	}
	
	@PostMapping("/review/add")
	public Long addReview(@Valid @RequestBody Review review) throws UserNotFoundException, ComicNotFoundException, ReviewImputDataException {
		Calendar date = Calendar.getInstance();
		Long creator_id = review.getCreator().getUserId();		
		User creator = getUserById(creator_id);
		boolean b = true;
		
		review.setCreator(creator);
		review.setDate(date);
		
		if (review.getType().equals(ReviewType.COMIC)) {
			Long comic_id = review.getReceiverComic().getComicId();
			Comic comic = getComicById(comic_id);
			review.setReceiverComic(comic);
			
			List<Review> comicReviews = comicservice.findALLReviewsByCreatorAndComic(creator, comic);
			b = comicReviews.isEmpty();
			
		} else if (review.getType().equals(ReviewType.USER)) {
			Long user_id = review.getReceiverUser().getUserId();
			User user = getUserById(user_id);
			review.setReceiverUser(user);
			
			List<Review> userReviews = comicservice.findALLReviewsByCreatorAndUser(creator, user);
			b = userReviews.isEmpty();
		}
		if(b) {
			return comicservice.insert(review);			
		} else {
			return (long) 0;
		}
	}
	
	@GetMapping("/review/{id}")
	public Review getReviewById(@PathVariable(value = "id") Long reviewId) throws ReviewNotFoundException {
		Optional<Review> review = comicservice.findReviewById(reviewId);
		
		try {
			review.get().getReviewId();
		}
		catch(Exception e) {
			throw new ReviewNotFoundException("Valoración no encontrada");
		}
		
		return review.get();
	}
	
	@PostMapping("/review/delete")
	public void deleteReview(@Valid @RequestBody Review review) {
		comicservice.delete(review);
	}
	
	@GetMapping("/comic/search")
	public String search(String scope, String filter, String criteria) {
		
		String url = "https://comicvine.gamespot.com/api/" + scope + "/?api_key=" + api_key + "&format=json&sort=name:asc&filter=" + filter + ":" + criteria;

		try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
            HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
            
            return response.toString();
            
        } catch (Exception ex) {
        	ex.printStackTrace();

        }
		return null;
		
	}
	
	@PostMapping("/comic/add")
	public Long addComic(@Valid @RequestBody Comic comic, Long user_id) throws UserNotFoundException {	
		Integer issue = -1;
		if (comic.getIssue() != null) {
			issue = comic.getIssue();
		}
		
		String publisher = "";
		if (comic.getPublisher() != null) {
			publisher = comic.getPublisher();
		}
		
		String title = "";
		if (comic.getTitle() != null) {
			title = comic.getTitle();
		}
		
		Integer volume = -1;
		if (comic.getVolume() != null) {
			volume = comic.getVolume();
		}
		
		Optional<Comic> comicbook = comicservice.findComic(issue, publisher, title, volume);
		
		User user = getUserById(user_id);
		
		if (comicbook.isEmpty()) {
			comic.getOwners().add(user);
			comic.setAmount(1);
			return comicservice.insert(comic);
		} else {
			comic = comicbook.get();
			comic.getOwners().add(user);
			int amount = comicbook.get().getAmount();
			long id = comicbook.get().getComicId();
			comic.setAmount(amount+1);
			comic.setComicId(id);
			return comicservice.update(comic);
		}	
	}
	
	@GetMapping("/comic/{id}")
	public Comic getComicById(@PathVariable(value = "id") Long comicId) throws ComicNotFoundException {
		Optional<Comic> comic = comicservice.findComicById(comicId);
		
		try {
			comic.get().getComicId();
		}
		catch(Exception e) {
			throw new ComicNotFoundException("Comic no disponible");
		}
		
		return comic.get();
	}
	
	@PostMapping("/comic/delete")
	public void deleteComic(@Valid @RequestBody Comic comic, Long user_id) throws UserNotFoundException {
		
		Integer issue = -1;
		if (comic.getIssue() != null) {
			issue = comic.getIssue();
		}
		
		String publisher = "";
		if (comic.getPublisher() != null) {
			publisher = comic.getPublisher();
		}
		
		String title = "";
		if (comic.getTitle() != null) {
			title = comic.getTitle();
		}
		
		Integer volume = -1;
		if (comic.getVolume() != null) {
			volume = comic.getVolume();
		}
		
		Optional<Comic> comicbook = comicservice.findComic(issue, publisher, title, volume);
		
		comic = comicbook.get();
		User user = getUserById(user_id);
		boolean b = comic.getOwners().contains(user);
		int size = comic.getOwners().size();
		if (b) {
			List <ComicPrice> comicPriceList = comicservice.findAllComicPrice(comic, user);
			for (ComicPrice c : comicPriceList)
				comicservice.delete(c);			
			}
			if (size > 1) {
				int amount = comic.getAmount();
				comic.setAmount(amount-1);
				comic.getOwners().remove(user);
				boolean b1 = comic.getSellers().contains(user);
				if (b1) {
					comic.getSellers().remove(user);
				}
				comicservice.update(comic);
			} else if (size == 1) {
				List<Review> reviews = comicservice.findReviewByComic(comic.getComicId());
				
				for (Review review: reviews)
					comicservice.delete(review);
				
				comicservice.delete(comic);	
			}
	}
	
	@PostMapping("/review/modify")
	public Long modifyReview(@Valid @RequestBody Review review) throws ReviewImputDataException {
		
		Calendar date = Calendar.getInstance();
		review.setDate(date);
		
		return comicservice.update(review);		
	}
	
	@PostMapping("/comicPrice/modify")
	public Long modifycomicPrice(@Valid @RequestBody ComicPrice comicPrice) throws UserImputDataException {
		
		if (comicPrice.getPrice() > 0) {
			return comicservice.update(comicPrice);
		} else 
			throw new UserImputDataException("Por favor, introduzca un valor numerico superior a 0");
	}
	
	@PostMapping("/comic/addToList")
	public Long addComicToList(Long comic_id, Long user_id, float price, int n) throws UserNotFoundException, ComicNotFoundException {	
		Comic comic = getComicById(comic_id);
		User user = getUserById(user_id);
		int c1 = 0, c2 = 0;
		
		ComicPrice comicPrice = new ComicPrice();
		comicPrice.setComic(comic);
		comicPrice.setUser(user);
		comicPrice.setPrice(price);
		
		if (n == 1) {
			if (comic.getOwners().contains(user)) {		
				List<Comic> comics = user.getOwned_comics();
				for (Comic c : comics)
					if (c == comic) {		
						c1++;
					}
				
				if (c1 > 0) {
					List <ComicPrice> comicPriceList = comicservice.findAllComicPrice(comic, user);		
					c2 = comicPriceList.size();	
				}
				
				if (c1 > c2) {
					return comicservice.insert(comicPrice);
				} else return (long) 0;
			}
		} else {
			if (!user.getDesired_comics().contains(comic)) {
				comic.getUsers().add(user);	
			}
		}
		return comicservice.update(comic);
	}
	
	@PostMapping("/comic/removeFromList")
	public void removeComicFromList(Long comic_id, Long user_id, float price, int n) throws UserNotFoundException, ComicNotFoundException {	
		Comic comic = getComicById(comic_id);
		User user = getUserById(user_id);
				
		if (n == 1) {
			List <ComicPrice> comicPriceList = comicservice.findAllComicPrice(comic, user);		
			
			for (ComicPrice cp : comicPriceList)
				if (cp.getPrice() == price) {
					comicservice.delete(cp);
				}
		} else {
			comic.getUsers().remove(user);
		}
		
		comicservice.update(comic);
	}
	
	@PostMapping("/transaction/add")
	public Long addTransaction(@Valid @RequestBody Transaction transaction) throws UserNotFoundException, ComicNotFoundException {	
		Calendar date = Calendar.getInstance();		
		List<ComicPrice> comicPriceList = transaction.getComicPriceList();
		List<Comic> comicList = new ArrayList<Comic>();
		int size = comicPriceList.size();
		float total_price;
		boolean b = true;
		
		Optional<Comic> comic = comicservice.findComicById(comicPriceList.get(0).getComic().getComicId());
		
		comicList.add(comic.get());
		total_price = comicPriceList.get(0).getPrice();
		
	    for (int i = 0; i < size-1; i++) {
	    	if (comicPriceList.get(i).getUser().getUserId() != comicPriceList.get(i+1).getUser().getUserId()) {
	    		b = false;
	    		break;
	    	}
	    	comic = comicservice.findComicById(comicPriceList.get(i+1).getComic().getComicId());
	    	comicList.add(comic.get());
	    	total_price = total_price + comicPriceList.get(i+1).getPrice();
	    }
	    
	    if (b) {
	    	Long buyerId = transaction.getBuyer().getUserId();
	    	Long sellerId = comicPriceList.get(0).getUser().getUserId();
	    	
			User buyer = comicservice.findUserById(buyerId).get();
			User seller = comicservice.findUserById(sellerId).get();
			
			transaction.setBuyer(buyer);
			transaction.setSeller(seller);
			transaction.setTotal_price(total_price);
			transaction.setDate(date);
			
			for (Comic c : comicList) {
				if (c.getOwners().contains(seller)) {
					c.getOwners().remove(seller);
					c.getOwners().add(buyer);
				} else return (long) 0;
			}
			
			for (ComicPrice cp : comicPriceList) {
					ComicPrice cpbd = comicservice.findComicPriceById(cp.getId()).get();
					if (cpbd.equals(null)) {
						return (long) 0;
					} else comicservice.delete(cpbd);			
				}
			
			return comicservice.insert(transaction);
	    }
	    return (long) 0;
	}
	
    @GetMapping("/createFacebookAuthorization")
    public String createFacebookAuthorization(){
        FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(facebookAppId, facebookSecret);
        OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();
        params.setRedirectUri("https://localhost:8080/ComicFriends/facebook");
        params.setScope("public_profile,email,user_birthday");
        return oauthOperations.buildAuthorizeUrl(params);
    }
    
    @GetMapping("/facebook")
    public void createFacebookAccessToken(@RequestParam("code") String code){
        FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(facebookAppId, facebookSecret);
        AccessGrant accessGrant = connectionFactory.getOAuthOperations().exchangeForAccess(code, "https://localhost:8080/ComicFriends/facebook", null);
        accessToken = accessGrant.getAccessToken();
    }

    @GetMapping("/insert")
    public Long insert() throws UserImputDataException{
        Facebook facebook = new FacebookTemplate(accessToken);
        String[] fields = {"id", "email", "name"};
        
        org.springframework.social.facebook.api.User user = facebook.fetchObject("me", org.springframework.social.facebook.api.User.class, fields);
        String name = user.getName();
        String email = user.getEmail();
        
        Optional<User> user1 = comicservice.findUserByEmail(user.getEmail());
        
        if (!user1.isEmpty()) {
			throw new UserImputDataException("Error: Email ya existente.");
		}
        
        com.app.ComicFriends.entities.User userDB = new com.app.ComicFriends.entities.User();
        userDB.setName(name);
        userDB.setEmail(email);
        
        return comicservice.insert(userDB); 
    }  
    
}
