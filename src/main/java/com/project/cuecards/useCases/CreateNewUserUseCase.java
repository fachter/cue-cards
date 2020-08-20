package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.CreateNewUser;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.exceptions.UserAlreadyExistsException;
import com.project.cuecards.gateways.UserGateway;
import com.project.cuecards.viewModels.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateNewUserUseCase implements CreateNewUser {

    private final UserGateway userGateway;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CreateNewUserUseCase(UserGateway userGateway, PasswordEncoder passwordEncoder) {
        this.userGateway = userGateway;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void create(RegisterRequest registerRequest) throws UserAlreadyExistsException, InvalidDataException {
        if (hasInvalidData(registerRequest))
            throw new InvalidDataException();
        userGateway.addUser(getUserFromRegisterRequest(registerRequest));
    }

    private boolean hasInvalidData(RegisterRequest registerRequest) {
        return (registerRequest == null || registerRequest.getUsername() == null || stringIsEmptyOrNull(registerRequest.getUsername())
                || stringIsEmptyOrNull(registerRequest.getPassword()) || stringIsEmptyOrNull(registerRequest.getEmail()));
    }

    private boolean stringIsEmptyOrNull(String email) {
        return email == null || email.equals("");
    }

    private User getUserFromRegisterRequest(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setFullName(getFullName(registerRequest));
        return user;
    }

    private String getFullName(RegisterRequest registerRequest) {
        return !stringIsEmptyOrNull(registerRequest.getFullName()) ?
                registerRequest.getFullName() : registerRequest.getUsername();
    }
}