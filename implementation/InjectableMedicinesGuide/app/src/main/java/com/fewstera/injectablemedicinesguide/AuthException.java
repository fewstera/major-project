package com.fewstera.injectablemedicinesguide;

public class AuthException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable throwable) {
        super(message, throwable);
    }

}