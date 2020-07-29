package com.project.cuecards.exceptions;

public class UserAlreadyExistsException extends Exception {

    public UserAlreadyExistsException() {
        super("User already exists!");
    }
}
