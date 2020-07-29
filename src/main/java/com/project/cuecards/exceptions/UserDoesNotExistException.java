package com.project.cuecards.exceptions;

public class UserDoesNotExistException extends Exception {

    public UserDoesNotExistException() {
        super("User does not exists");
    }
}
