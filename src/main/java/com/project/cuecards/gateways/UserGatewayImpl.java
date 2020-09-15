package com.project.cuecards.gateways;

import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.UserAlreadyExistsException;
import com.project.cuecards.exceptions.UserDoesNotExistException;
import com.project.cuecards.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserGatewayImpl implements UserGateway {

    private final UserRepository userRepository;

    @Autowired
    public UserGatewayImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserById(Long id) throws UserDoesNotExistException {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty())
            throw new UserDoesNotExistException();
        return user.get();
    }

    @Override
    public User getUserByUsername(String username) throws UserDoesNotExistException {
        User user = userRepository.findUserByUsername(username);
        if (user == null)
            throw new UserDoesNotExistException();
        return user;
    }

    @Override
    public void addUser(User cueCarder) throws UserAlreadyExistsException {
        try {
            userRepository.save(cueCarder);
        } catch (Exception e) {
            throw new UserAlreadyExistsException();
        }
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public List<User> getUserByUsernameOrEmail(String username, String email) {
        return userRepository.findUsersByUsernameOrEmail(username, email);
    }
}
