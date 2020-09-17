package com.project.cuecards.viewModels;

public class AuthenticationResponse {

    public final String jwt;
    public final UserViewModel userData;

    public AuthenticationResponse(String jwt, UserViewModel userData) {
        this.jwt = jwt;
        this.userData = userData;
    }
}
