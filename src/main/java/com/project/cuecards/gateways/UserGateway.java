package com.project.cuecards.gateways;

import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.UserAlreadyExistsException;
import com.project.cuecards.exceptions.UserDoesNotExistException;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserGateway {

    User getUserById(Long id) throws UserDoesNotExistException;

    User getUserByUsername(String username) throws UserDoesNotExistException;

    void addUser(User cueCarder) throws UserAlreadyExistsException;

    void saveUser(User user);

    List<User> getUserByUsernameOrEmail(String username, String email);
}
