package com.app.ComicFriends.entities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "transactions")
public class Transaction {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRANSACTION_ID")
	private Long transactionId;
	
	@Column(name = "DATE")
	private Calendar date;
	
	@Column(name = "PRICE")
	private float total_price;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BUYER_ID")
	private User buyer;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SELLER_ID")
	private User seller;
	
	@OneToMany(mappedBy = "transaction")
	private List<ComicPrice> comicPriceList = new ArrayList<>();
	
	
	// Default constructor
	protected Transaction() {
    }

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public float getTotal_price() {
		return total_price;
	}

	public void setTotal_price(float total_price) {
		this.total_price = total_price;
	}

	public User getBuyer() {
		return buyer;
	}
	
	public void setBuyer(User buyer) {
		this.buyer = buyer;
	}

	public User getSeller() {
		return seller;
	}
	
	public void setSeller(User seller) {
		this.seller = seller;
	}

	public List<ComicPrice> getComicPriceList() {
		return comicPriceList;
	}

	public void setComicPriceList(List<ComicPrice> comicPriceList) {
		this.comicPriceList = comicPriceList;
	}
	
	@Override
	public String toString() {
		return "Transaction [transactionId=" + transactionId + ", date=" + date + ", total_price=" + total_price
				+ ", buyer=" + buyer + ", seller=" + seller + ", comicPriceList=" + comicPriceList + "]";
	}
	
}