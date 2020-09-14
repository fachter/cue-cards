package com.project.cuecards.services;

import com.project.cuecards.utils.JwtUtil;
import com.project.cuecards.viewModels.AuthenticationRequest;
import com.project.cuecards.viewModels.AuthenticationResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateServiceImpl implements AuthenticateService {

    private final AuthenticationManager authenticationManager;
    private final UserRepositoryUserDetailsService userDetailsService;
    private final JwtUtil jwtTokenUtil;

    public AuthenticateServiceImpl(AuthenticationManager authenticationManager,
                                   UserRepositoryUserDetailsService userDetailsService,
                                   JwtUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()));

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return new AuthenticationResponse(jwt);
    }
}
