package com.project.cuecards.services;

import com.project.cuecards.exceptions.UserDoesNotExistException;
import com.project.cuecards.gateways.UserGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserRepositoryUserDetailsService implements UserDetailsService {

    private final UserGateway userGateway;

    @Autowired
    public UserRepositoryUserDetailsService(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return userGateway.getUserByUsername(username);
        } catch (UserDoesNotExistException e) {
            throw new UsernameNotFoundException("User with the username '" + username + "' not found.");
        }
    }
}