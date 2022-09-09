package com.app.ComicFriends.entities;
 
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "comics_sellers")
public class ComicPrice {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMIC_SELLER_ID")
	private Long id;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMIC_ID")   
    private Comic comic;
     
    @Column(name = "PRICE")
    private float price;
    
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comicPriceList")
	private Transaction transaction;
	

	// Default constructor
	public ComicPrice() {
    }
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Comic getComic() {
		return comic;
	}

	public void setComic(Comic comic) {
		this.comic = comic;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "ComicPrice [id=" + id + ", user=" + user + ", comic=" + comic + ", price=" + price + ", transaction="
				+ transaction + "]";
	}
    
}
