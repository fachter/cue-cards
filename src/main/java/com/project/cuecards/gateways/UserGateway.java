package com.project.cuecards.gateways;

import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.UserAlreadyExistsException;
import com.project.cuecards.exceptions.UserDoesNotExistException;
import org.springframework.stereotype.Service;

@Service
public interface UserGateway {

    User getUserByUsername(String username) throws UserDoesNotExistException;

    void addUser(User cueCarder) throws UserAlreadyExistsException;
}
