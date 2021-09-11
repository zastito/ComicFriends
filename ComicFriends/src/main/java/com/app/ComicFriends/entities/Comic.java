package com.app.ComicFriends.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "comics")
public class Comic {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMIC_ID")
	private Long comicId;
	
	@ElementCollection
	@Column(name = "CREATORS")
	private List<String> creators = new ArrayList<>();
	
	@Column(name = "TITLE")
	private String title;
	
	@Column(name = "PUBLISHER")
	private String publisher;
	
	@Column(name = "VOLUME")
	private Integer volume;
	
	@Column(name = "ISSUE")
	private Integer issue;
	
	@Column(name = "AMOUNT")
	private Integer amount;
	
    @JoinTable(
            name = "comics_owners",
            joinColumns = @JoinColumn(name = "fk_comic", nullable = false),
            inverseJoinColumns = @JoinColumn(name="fk_owner", nullable = false)
    )
	@ManyToMany()
	@JoinColumn(name = "OWNER_ID")
	private List<User> owners = new ArrayList<>();

    @OneToMany(mappedBy = "comic")
	private List<ComicPrice> sellers = new ArrayList<>();
	
    @JoinTable(
            name = "comics_users",
            joinColumns = @JoinColumn(name = "fk_comic", nullable = false),
            inverseJoinColumns = @JoinColumn(name="fk_user", nullable = false)
    )
	@ManyToMany()
	@JoinColumn(name = "USER_ID")
	private List<User> users = new ArrayList<>();
	
	@OneToMany(mappedBy = "receiverComic")
	private List<Review> received_reviews = new ArrayList<>();
	
	
	// Default constructor
	public Comic() {
    }
	
	public Long getComicId() {
		return comicId;
	}
	
	public void setComicId(Long comicId) {
		this.comicId = comicId;
	}
	
	public List<String> getCreators() {
		return creators;
	}

	public void setCreators(List<String> creators) {
		this.creators = creators;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getPublisher() {
		return publisher;
	}
	
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	
	public Integer getVolume() {
		return volume;
	}
	
	public void setVolume(Integer volume) {
		this.volume = volume;
	}
	
	public Integer getIssue() {
		return issue;
	}
	
	public void setIssue(Integer issue) {
		this.issue = issue;
	}
	
	public Integer getAmount() {
		return amount;
	}
	
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	
    public List<User> getOwners() {
		return owners;
	}

	public List<ComicPrice> getSellers() {
		return sellers;
	}

	public List<User> getUsers() {
		return users;
	}

	@Override
	public String toString() {
		return "Comic [comicId=" + comicId + ", creators=" + creators + ", title=" + title + ", publisher=" + publisher
				+ ", volume=" + volume + ", issue=" + issue + ", amount=" + amount + "]";
	}
	
}