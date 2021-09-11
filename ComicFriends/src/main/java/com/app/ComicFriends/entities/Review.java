package com.app.ComicFriends.entities;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "reviews")
public class Review {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEW_ID")
	private Long reviewId;
	
	@Column(name = "TITLE")
	private String title;
	
	@Column(name = "COMMENT")
	private String comment;
	
	@Column(name = "DATE")
	private Calendar date;
	
	@Column(name = "SCORE")
	private Integer score;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "TYPE")
	private ReviewType type;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CREATOR_ID")
	private User creator;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RECEIVER_USER_ID")
	private User receiverUser;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RECEIVER_COMIC_ID")
	private Comic receiverComic;
	
	
	// Default constructor
	protected Review() {
    }

	public Long getReviewId() {
		return reviewId;
	}

	public void setReviewId(Long reviewId) {
		this.reviewId = reviewId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public ReviewType getType() {
		return type;
	}

	public void setType(ReviewType type) {
		this.type = type;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public User getReceiverUser() {
		return receiverUser;
	}

	public void setReceiverUser(User receiverUser) {
		this.receiverUser = receiverUser;
	}

	public Comic getReceiverComic() {
		return receiverComic;
	}

	public void setReceiverComic(Comic receiverComic) {
		this.receiverComic = receiverComic;
	}

	@Override
	public String toString() {
		return "Review [reviewId=" + reviewId + ", title=" + title + ", comment=" + comment + ", date=" + date
				+ ", score=" + score + ", type=" + type + "]";
	}
	
}