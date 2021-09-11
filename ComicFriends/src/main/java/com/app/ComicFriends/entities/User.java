package com.app.ComicFriends.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
	private Long userId;
	
	@Column(name = "EMAIL")
	private String email;
	
	@Column(name = "USER")
	private String username;
	
	@Column(name = "PASSWORD")
	private String password;
	
	@Column(name = "NAME")
	private String name;
	
	@Column(name = "SEX")
	private String sex;
	
	@Column(name = "COUNTRY")
	private String country;
	
	@Column(name = "DESCRIPTION")
	private String description;
	
	@ManyToMany(mappedBy = "owners", cascade = CascadeType.ALL)
	private List<Comic> owned_comics = new ArrayList<>();
	   
	@OneToMany(mappedBy = "user")
	private List<ComicPrice> forSale_comics = new ArrayList<>();
	
	@ManyToMany(mappedBy = "users", cascade = CascadeType.ALL)
	private List<Comic> desired_comics = new ArrayList<>();
	
	@OneToMany(mappedBy = "creator")
	private List<Review> writed_reviews = new ArrayList<>();
	
	@OneToMany(mappedBy = "receiverUser")
	private List<Review> received_reviews = new ArrayList<>();
	
	@OneToMany(mappedBy = "buyer")
	private List<Transaction> buyer_transactions = new ArrayList<>();
	
	@OneToMany(mappedBy = "seller")
	private List<Transaction> seller_transactions = new ArrayList<>();
	
	// Default constructor
	public User() {
    }

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	

	public List<Comic> getOwned_comics() {
		return owned_comics;
	}

	public void setOwned_comics(List<Comic> owned_comics) {
		this.owned_comics = owned_comics;
	}

	public List<ComicPrice> getForSale_comics() {
		return forSale_comics;
	}

	public void setForSale_comics(List<ComicPrice> forSale_comics) {
		this.forSale_comics = forSale_comics;
	}

	public List<Comic> getDesired_comics() {
		return desired_comics;
	}

	public void setDesired_comics(List<Comic> desired_comics) {
		this.desired_comics = desired_comics;
	}

	public List<Review> getWrited_reviews() {
		return writed_reviews;
	}

	public void setWrited_reviews(List<Review> writed_reviews) {
		this.writed_reviews = writed_reviews;
	}

	public List<Review> getReceived_reviews() {
		return received_reviews;
	}

	public void setReceived_reviews(List<Review> received_reviews) {
		this.received_reviews = received_reviews;
	}

	public List<Transaction> getBuyer_transactions() {
		return buyer_transactions;
	}

	public void setBuyer_transactions(List<Transaction> buyer_transactions) {
		this.buyer_transactions = buyer_transactions;
	}

	public List<Transaction> getSeller_transactions() {
		return seller_transactions;
	}

	public void setSeller_transactions(List<Transaction> seller_transactions) {
		this.seller_transactions = seller_transactions;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", email=" + email + ", username=" + username + ", password=" + password
				+ ", name=" + name + ", sex=" + sex + ", country=" + country + ", description=" + description + "]";
	}
	
}