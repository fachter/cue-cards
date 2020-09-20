package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.CreateNewUser;
import com.project.cuecards.entities.Role;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.*;
import com.project.cuecards.gateways.RoleGateway;
import com.project.cuecards.gateways.UserGateway;
import com.project.cuecards.services.ValidateUserDataInputService;
import com.project.cuecards.viewModels.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreateNewUserUseCase implements CreateNewUser {

    private final ValidateUserDataInputService userDataInputService;
    private final UserGateway userGateway;
    private final PasswordEncoder passwordEncoder;
    private final RoleGateway roleGateway;

    @Autowired
    public CreateNewUserUseCase(ValidateUserDataInputService userDataInputService,
                                UserGateway userGateway,
                                PasswordEncoder passwordEncoder,
                                RoleGateway roleGateway) {
        this.userDataInputService = userDataInputService;
        this.userGateway = userGateway;
        this.passwordEncoder = passwordEncoder;
        this.roleGateway = roleGateway;
    }

    @Override
    public void create(RegisterRequest registerRequest) throws UserAlreadyExistsException, InvalidDataException, EmailAlreadyExistsException, UsernameAlreadyExistsException {
        if (hasInvalidData(registerRequest))
            throw new InvalidDataException();
        checkIfUsernameOrEmailAlreadyExists(registerRequest.getUsername(), registerRequest.getEmail());
        userGateway.addUser(getUserFromRegisterRequest(registerRequest));
    }

    private void checkIfUsernameOrEmailAlreadyExists(String username, String email) throws EmailAlreadyExistsException, UserAlreadyExistsException, UsernameAlreadyExistsException {
        List<User> existingUsers = userGateway.getUserByUsernameOrEmail(username, email);
        if (existingUsers.size() > 0) {
            boolean usernameExists = false;
            boolean emailExists = false;
            for (User existingUser : existingUsers) {
                if (existingUser.getUsername().equals(username))
                    usernameExists = true;
                if (existingUser.getEmail().equals(email))
                    emailExists = true;
            }
            throwProperException(usernameExists, emailExists);
        }
    }

    private void throwProperException(boolean usernameExists, boolean emailExists) throws UsernameAlreadyExistsException, EmailAlreadyExistsException, UserAlreadyExistsException {
        if (usernameExists && !emailExists)
            throw new UsernameAlreadyExistsException();
        else if (emailExists && !usernameExists)
            throw new EmailAlreadyExistsException();
        else if (emailExists)
            throw new UserAlreadyExistsException();
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
        user.setNickName(getFullName(registerRequest));
        user.getRoles().add(getRole());
        return user;
    }

    private String getFullName(RegisterRequest registerRequest) {
        return !stringIsEmptyOrNull(registerRequest.getFullName()) ?
                registerRequest.getFullName() : registerRequest.getUsername();
    }

    private Role getRole() {
        try {
            return roleGateway.getUserRole();
        } catch (RoleDoesNotExistException e) {
            Role role = new Role();
            role.setName("USER");
            role.setDescription("User Role");
            return role;
        }
    }
}