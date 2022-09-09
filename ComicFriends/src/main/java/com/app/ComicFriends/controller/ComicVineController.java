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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.app.ComicFriends.entities.Comic;
import com.app.ComicFriends.entities.ComicPrice;
import com.app.ComicFriends.entities.Review;
import com.app.ComicFriends.entities.ReviewType;
import com.app.ComicFriends.entities.Transaction;
import com.app.ComicFriends.entities.User;
import com.app.ComicFriends.exception.ComicNotFoundException;
import com.app.ComicFriends.exception.ComicPriceNotFoundException;
import com.app.ComicFriends.exception.ReviewImputDataException;
import com.app.ComicFriends.exception.ReviewNotFoundException;
import com.app.ComicFriends.exception.UserImputDataException;
import com.app.ComicFriends.exception.UserNotFoundException;
import com.app.ComicFriends.service.ComicFriendsService;

@RestController
@CrossOrigin(origins = "https://192.168.0.16:4200")
//@CrossOrigin(origins = "https://angular-kuaz1g.stackblitz.io")
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
	
	@GetMapping("/users/{id}") //backend version
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
	
	@GetMapping("/user/{id}") //frontend version
	public User getUserById2(@PathVariable(value = "id") Long userId) throws UserNotFoundException {
		Optional<User> user = comicservice.findUserById(userId);
		
		try {
			user.get().getUserId();
		}
		catch(Exception e) {
			throw new UserNotFoundException("Usuario no encontrado");
		}
		
		User u = user.get();
		
		u.setOwned_comics(null);
		u.setDesired_comics(null);
		u.setForSale_comics(null);
		u.setReceived_reviews(null);
		u.setWrited_reviews(null);
		u.setBuyer_transactions(null);
		u.setSeller_transactions(null);
		
		return u;
	}
	
	@GetMapping("/user/{username}/{password}")
	public User getUserByNameAndPassword(@PathVariable(value = "username") String username, @PathVariable(value = "password") String password) throws UserNotFoundException {
		Optional<User> user = comicservice.findUserByName(username);
		
		try {
			user.get().getUserId();
		}
		catch(Exception e) {
			throw new UserNotFoundException("Usuario no encontrado");
		}
		
		if(!user.get().getPassword().equals(password)) {
			throw new UserNotFoundException("Contraseña incorrecta");
		}
			
		User u = user.get();
		
		u.setOwned_comics(null);
		u.setDesired_comics(null);
		u.setForSale_comics(null);
		u.setReceived_reviews(null);
		u.setWrited_reviews(null);
		u.setBuyer_transactions(null);
		u.setSeller_transactions(null);
		
		return u;
	}
	
	@PostMapping("/user/modify")
	public User modifyUser(@Valid @RequestBody User user) throws UserImputDataException {
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
		
		comicservice.update(user);		
		return user; 
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
			Comic comic = getComicById2(comic_id);
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
		
		Review r = review.get();
		r.setCreator(null);
		
		if (r.getType().toString() == "COMIC") {
			String title = r.getReceiverComic().getTitle();
			
			Comic c = new Comic();
			if (title != null) {
				c.setTitle(title);
			}
			r.setReceiverComic(c);
		
			User u = r.getReceiverUser();
			if (u != null) {
				u.setOwned_comics(null);
				u.setDesired_comics(null);
				u.setForSale_comics(null);
				u.setReceived_reviews(null);
				u.setWrited_reviews(null);		
			}
			r.setReceiverUser(u);	
		}
		
		if (r.getType().toString() == "USER") {
			String username = r.getReceiverUser().getUsername();
			
			User u = new User();
			if (username != null) {
				u.setUsername(username);
			}
			r.setReceiverUser(u);
		
			Comic c = r.getReceiverComic();
			if (c != null) {
				c.setCreators(null);
				c.setOwners(null);
				c.setReceived_reviews(null);
				c.setSellers(null);
				c.setUsers(null);
			}
			r.setReceiverComic(c);	
		}
		
		return r;
	}
	
	@PostMapping("/review/delete")
	public void deleteReview(@Valid @RequestBody Review review) {
		comicservice.delete(review);
	}
	
	@GetMapping("/comic/search/{scope}/{filter}/{criteria}")
	public Object search(@PathVariable(value = "scope") String scope, @PathVariable(value = "filter") String filter, @PathVariable(value = "criteria") String criteria) {
		
		String url = "https://comicvine.gamespot.com/api/" + scope + "/?api_key=" + api_key + "&format=json&sort=name:asc&filter=" + filter + ":" + criteria + "&limit=50" + "&field_list=count_of_issues,name,publisher,site_detail_url,start_year,image";

		try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
            HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
            
            return response.getBody();
            
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
	
	public Long updateComic2(Comic comic) throws UserNotFoundException {	
			return comicservice.update(comic);
	}
	
	@GetMapping("/comic/{id}") //frontend version
	public Comic getComicById(@PathVariable(value = "id") Long comicId) throws ComicNotFoundException {
		Optional<Comic> comic = comicservice.findComicById(comicId);
		
		try {
			comic.get().getComicId();
		}
		catch(Exception e) {
			throw new ComicNotFoundException("Comic no disponible");
		}
		
		Comic c = comic.get();
		c.setOwners(null);
		c.setUsers(null);
		c.setSellers(null);
		c.setReceived_reviews(null);
		return c;
	}
	
	public Comic getComicById2(Long comicId) throws ComicNotFoundException {  //backend version
		Optional<Comic> comic = comicservice.findComicById(comicId);
		
		try {
			comic.get().getComicId();
		}
		catch(Exception e) {
			throw new ComicNotFoundException("Comic no disponible");
		}
		
		Comic c = comic.get();
		return c;
	}
	
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	@ResponseBody
	public List<User> getUsers() {
		List<User> users = comicservice.findALLUsers();
		List<User> users2 = new ArrayList<User>();
		
		for (User u : users) {
			u.setOwned_comics(null);
			u.setDesired_comics(null);
			u.setForSale_comics(null);
			u.setReceived_reviews(null);
			u.setWrited_reviews(null);
			u.setBuyer_transactions(null);
			u.setSeller_transactions(null);
			users2.add(u);
		}
		
		return users2;
	}
	
	@RequestMapping(value = "/comics", method = RequestMethod.GET)
	@ResponseBody
	public List<Comic> getComics() {
		List<Comic> comics = comicservice.findALLComics();
		List<Comic> comics2 = new ArrayList<Comic>();
		
		for (Comic c : comics) {
			c.setOwners(null);
			c.setUsers(null);
			c.setSellers(null);
			c.setReceived_reviews(null);
			comics2.add(c);
		}
		
		return comics2;
	}
	
	@RequestMapping(value = "/reviews", method = RequestMethod.GET)
	@ResponseBody
	public List<Review> getReviews() {
		List<Review> reviews = comicservice.findALLReviews();
		List<Review> reviews2 = new ArrayList<Review>();
		
		for (Review r : reviews) {
			User u2 = new User();
			
			u2.setOwned_comics(null);
			u2.setDesired_comics(null);
			u2.setForSale_comics(null);
			u2.setReceived_reviews(null);
			u2.setWrited_reviews(null);
			u2.setBuyer_transactions(null);
			u2.setSeller_transactions(null);
				
			u2.setUsername(r.getCreator().getUsername());
			u2.setUserId(r.getCreator().getUserId());
			
			r.setCreator(u2);
			
			if (r.getType().toString() == "COMIC") {
				String title = r.getReceiverComic().getTitle();
				
				Comic c = new Comic();
				if (title != null) {
					c.setTitle(title);
				}
				r.setReceiverComic(c);
			
				User u = r.getReceiverUser();
				if (u != null) {
					u.setOwned_comics(null);
					u.setDesired_comics(null);
					u.setForSale_comics(null);
					u.setReceived_reviews(null);
					u.setWrited_reviews(null);
					u.setBuyer_transactions(null);
					u.setSeller_transactions(null);
				}
				r.setReceiverUser(u);	
			}
			
			if (r.getType().toString() == "USER") {
				String username = r.getReceiverUser().getUsername();
				
				User u = new User();
				if (username != null) {
					u.setUsername(username);
				}
				r.setReceiverUser(u);
			
				Comic c = r.getReceiverComic();
				if (c != null) {
					c.setCreators(null);
					c.setOwners(null);
					c.setReceived_reviews(null);
					c.setSellers(null);
					c.setUsers(null);
				}
				r.setReceiverComic(c);	
			}
			
			reviews2.add(r);
		}
		
		return reviews2;
	}
	
	@GetMapping("/comic/name/{title}")
	public Comic getComicByTitle(@PathVariable(value = "title") String title) throws ComicNotFoundException {
		Optional<Comic> comic = comicservice.findComicByTitle(title);
		
		try {
			comic.get().getComicId();
		}
		catch(Exception e) {
			throw new ComicNotFoundException("Comic no encontrado");
		}
		
		Comic c = comic.get();
		c.setOwners(null);
		c.setUsers(null);
		c.setSellers(null);
		c.setReceived_reviews(null);
		return c;
	}
	
	@GetMapping("/comic/owned/{username}")
	public List<Comic> getComicsByUser(@PathVariable(value = "username") String username) throws UserNotFoundException {
		Optional<User> user = comicservice.findUserByName(username);
		
		try {
			user.get().getUserId();
		}
		catch(Exception e) {
			throw new UserNotFoundException("Usuario no encontrado");
		}
			
		User u = user.get();
		
		List<Comic> comics = u.getOwned_comics();
		List<Comic> comics2 = new ArrayList<Comic>();
		
		for (Comic c : comics) {
			c.setOwners(null);
			c.setUsers(null);
			c.setSellers(null);
			c.setReceived_reviews(null);
			comics2.add(c);
		}
		
		return comics2;

	}
	
	@GetMapping("/comic/desired/{username}")
	public List<Comic> getDesiredByUser(@PathVariable(value = "username") String username) throws UserNotFoundException {
		Optional<User> user = comicservice.findUserByName(username);
		
		try {
			user.get().getUserId();
		}
		catch(Exception e) {
			throw new UserNotFoundException("Usuario no encontrado");
		}
			
		User u = user.get();
		
		List<Comic> comics = u.getDesired_comics();
		List<Comic> comics2 = new ArrayList<Comic>();
		
		for (Comic c : comics) {
			c.setOwners(null);
			c.setUsers(null);
			c.setSellers(null);
			c.setReceived_reviews(null);
			comics2.add(c);
		}
		
		return comics2;

	}
	
	@RequestMapping(value = "/comicPrices", method = RequestMethod.GET)
	@ResponseBody
	public List<ComicPrice> getComicPrices() {
		List<ComicPrice> comicPrice = comicservice.findALLComicPrices();
		List<ComicPrice> comicsPrice2 = new ArrayList<ComicPrice>();
		
		for (ComicPrice cp : comicPrice) {
			User u1 = cp.getUser();
			Comic c1 = cp.getComic();
			User u2 = new User();
			Comic c2 = new Comic();
			
			c2.setTitle(c1.getTitle());
			c2.setPublisher(c1.getPublisher());
			c2.setVolume(c1.getVolume());
			c2.setIssue(c1.getIssue());	
			c2.setComicId(c1.getComicId());
			
			u2.setUsername(u1.getUsername());
			u2.setUserId(u1.getUserId());
			
			cp.setUser(u2);
			cp.setComic(c2);
			
			comicsPrice2.add(cp);
		}
		
		return comicsPrice2;

	}
	
	@GetMapping("/ComicPrice/find/{comicId}/{userId}")
	public List <ComicPrice> findComicPrice(@PathVariable(value = "comicId") Long comicId, @PathVariable(value = "userId") Long userId) throws ComicPriceNotFoundException, ComicNotFoundException, UserNotFoundException {
		
		Optional<User> user = comicservice.findUserById(userId);
		
		try {
			user.get().getUserId();
		}
		catch(Exception e) {
			throw new UserNotFoundException("Usuario no encontrado");
		}
		
		User u = user.get();
		
		u.setOwned_comics(null);
		u.setDesired_comics(null);
		u.setForSale_comics(null);
		u.setReceived_reviews(null);
		u.setWrited_reviews(null);
		u.setBuyer_transactions(null);
		u.setSeller_transactions(null);
		
		Optional<Comic> comic = comicservice.findComicById(comicId);
		
		try {
			comic.get().getComicId();
		}
		catch(Exception e) {
			throw new ComicNotFoundException("Comic no disponible");
		}
		
		Comic c = comic.get();
		c.setOwners(null);
		c.setUsers(null);
		c.setSellers(null);
		c.setReceived_reviews(null);
		
		List <ComicPrice> comicPrice = comicservice.findAllComicPrice(c, u);
		List<ComicPrice> comicsPrice2 = new ArrayList<ComicPrice>();
		
		for (ComicPrice cp : comicPrice) {
			User u1 = cp.getUser();
			Comic c1 = cp.getComic();
			User u2 = new User();
			Comic c2 = new Comic();
			
			c2.setTitle(c1.getTitle());
			c2.setPublisher(c1.getPublisher());
			c2.setVolume(c1.getVolume());
			c2.setIssue(c1.getIssue());	
			c2.setComicId(c1.getComicId());
			
			u2.setUsername(u1.getUsername());
			u2.setUserId(u1.getUserId());
			
			cp.setUser(u2);
			cp.setComic(c2);
			
			comicsPrice2.add(cp);
		}
		
		return comicsPrice2;
	}
	
	@GetMapping("/ComicPrice/{id}")
	public ComicPrice getComicPriceById(@PathVariable(value = "id") Long comicPriceId) throws ComicPriceNotFoundException {
		Optional<ComicPrice> comicPrice = comicservice.findComicPriceById(comicPriceId);
		
		try {
			comicPrice.get().getId();
		}
		catch(Exception e) {
			throw new ComicPriceNotFoundException("Comic no puesto a la venta");
		}
		
		ComicPrice cp = comicPrice.get();
		
		User u1 = cp.getUser();
		Comic c1 = cp.getComic();
		User u2 = new User();
		Comic c2 = new Comic();
		
		c2.setTitle(c1.getTitle());
		c2.setPublisher(c1.getPublisher());
		c2.setVolume(c1.getVolume());
		c2.setIssue(c1.getIssue());	
		c2.setComicId(c1.getComicId());
		
		u2.setUsername(u1.getUsername());
		u2.setUserId(u1.getUserId());
		
		cp.setUser(u2);
		cp.setComic(c2);
		
		return cp;
	}
	
	@GetMapping("/comic/forsale/{username}")
	public List<Comic> getForSaleByUser(@PathVariable(value = "username") String username) throws UserNotFoundException {
		Optional<User> user = comicservice.findUserByName(username);
		
		try {
			user.get().getUserId();
		}
		catch(Exception e) {
			throw new UserNotFoundException("Usuario no encontrado");
		}
			
		User u = user.get();
		
		List<ComicPrice> comics = u.getForSale_comics();
		List<Comic> comics2 = new ArrayList<Comic>();
		
		for (ComicPrice cp : comics) {			
			User u1 = cp.getUser();
			Comic c1 = cp.getComic();
			User u2 = new User();
			Comic c2 = new Comic();
			
			c2.setTitle(c1.getTitle());
			c2.setPublisher(c1.getPublisher());
			c2.setVolume(c1.getVolume());
			c2.setIssue(c1.getIssue());	
			c2.setComicId(c1.getComicId());
			
			u2.setUsername(u1.getUsername());
			u2.setUserId(u1.getUserId());
			
			cp.setUser(u2);
			cp.setComic(c2);
			
			comics2.add(cp.getComic());
		}
		
		return comics2;

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
		Comic comic = getComicById2(comic_id);
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
		Comic comic = getComicById2(comic_id);
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
	    	Long sellerId = transaction.getSeller().getUserId();
	    	
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
        String password = user.getId();
        
        Optional<User> user1 = comicservice.findUserByEmail(user.getEmail());
        
        if (!user1.isEmpty()) {
        	return user1.get().getUserId();
		}
        
        com.app.ComicFriends.entities.User userDB = new com.app.ComicFriends.entities.User();
        userDB.setName(name);
        userDB.setEmail(email);
        userDB.setPassword(password);
        
        return comicservice.insert(userDB); 
    }  
    
}
