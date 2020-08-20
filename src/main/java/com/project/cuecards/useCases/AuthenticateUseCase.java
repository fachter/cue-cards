package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.Authenticate;
import com.project.cuecards.services.UserRepositoryUserDetailsService;
import com.project.cuecards.utils.JwtUtil;
import com.project.cuecards.viewModels.AuthenticationRequest;
import com.project.cuecards.viewModels.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateUseCase implements Authenticate {

    private final AuthenticationManager authenticationManager;
    private final UserRepositoryUserDetailsService userDetailsService;
    private final JwtUtil jwtTokenUtil;

    @Autowired
    public AuthenticateUseCase(AuthenticationManager authenticationManager,
                               UserRepositoryUserDetailsService userDetailsService,
                               JwtUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()));

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return new AuthenticationResponse(jwt);
    }
}