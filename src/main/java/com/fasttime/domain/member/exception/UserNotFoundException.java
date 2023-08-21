package com.fasttime.domain.member.exception;

public class UserNotFoundException extends Exception{
    public UserNotFoundException(String message){
        super(message);
    }
}