package com.project.cuecards.viewModels;

public class RegisterRequest {

    private String username;
    private String password;
    private String fullName;
    private String email;

    public RegisterRequest() {}

    public RegisterRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public RegisterRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public RegisterRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public RegisterRequest setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public RegisterRequest setEmail(String email) {
        this.email = email;
        return this;
    }
}