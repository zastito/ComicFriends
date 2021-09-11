package com.app.ComicFriends.exception;

public class ComicNotFoundException extends Exception {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ComicNotFoundException(String msg) {
        super(msg);
    }
}