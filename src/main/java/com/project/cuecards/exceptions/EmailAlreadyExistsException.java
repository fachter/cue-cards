package com.project.cuecards.exceptions;

public class EmailAlreadyExistsException extends Exception {
    public EmailAlreadyExistsException() {
        super("Email Already Exists");
    }
}
