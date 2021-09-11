package com.app.ComicFriends.exception;

public class ReviewNotFoundException extends Exception {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ReviewNotFoundException(String msg) {
        super(msg);
    }
}