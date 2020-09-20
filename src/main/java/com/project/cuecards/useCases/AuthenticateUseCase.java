package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.Authenticate;
import com.project.cuecards.services.AuthenticateService;
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

    private final AuthenticateService authenticateService;

    @Autowired
    public AuthenticateUseCase(AuthenticateService authenticateService) {
        this.authenticateService = authenticateService;
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        return authenticateService.authenticate(authenticationRequest);
    }
}