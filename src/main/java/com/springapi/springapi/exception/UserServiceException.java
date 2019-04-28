package com.springapi.springapi.exception;

public class UserServiceException extends RuntimeException{

    private static final long serialVersionUID = 1245356872222222L;


    public UserServiceException(String s) {
        super(s);
    }
}
