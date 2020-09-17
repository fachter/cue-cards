package com.project.cuecards.services;

import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.utils.JwtUtil;
import com.project.cuecards.viewModels.AuthenticationRequest;
import com.project.cuecards.viewModels.AuthenticationResponse;
import com.project.cuecards.viewModels.UserViewModel;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateServiceImpl implements AuthenticateService {

    private final AuthenticationManager authenticationManager;
    private final UserRepositoryUserDetailsService userDetailsService;
    private final JwtUtil jwtTokenUtil;
    private final UsersProfileDataService usersProfileDataService;

    public AuthenticateServiceImpl(AuthenticationManager authenticationManager,
                                   UserRepositoryUserDetailsService userDetailsService,
                                   JwtUtil jwtTokenUtil,
                                   UsersProfileDataService usersProfileDataService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.usersProfileDataService = usersProfileDataService;
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()));

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);
        UserViewModel userViewModel = null;
        try {
            userViewModel = usersProfileDataService.get((User) userDetails);
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }

        return new AuthenticationResponse(jwt, userViewModel);
    }
}
