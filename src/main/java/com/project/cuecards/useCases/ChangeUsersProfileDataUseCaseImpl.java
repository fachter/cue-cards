package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.ChangeUsersProfileDataUseCase;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.*;
import com.project.cuecards.gateways.UserGateway;
import com.project.cuecards.services.AuthenticateService;
import com.project.cuecards.services.ValidateUserDataInputService;
import com.project.cuecards.viewModels.AuthenticationRequest;
import com.project.cuecards.viewModels.AuthenticationResponse;
import com.project.cuecards.viewModels.ChangeUserViewModel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChangeUsersProfileDataUseCaseImpl implements ChangeUsersProfileDataUseCase {

    private final UserGateway userGateway;
    private final ValidateUserDataInputService validateUserDataInputService;
    private final AuthenticateService authenticateService;
    private final PasswordEncoder passwordEncoder;
    private User userById;

    public ChangeUsersProfileDataUseCaseImpl(UserGateway userGateway,
                                             ValidateUserDataInputService validateUserDataInputService, AuthenticateService authenticateService,
                                             PasswordEncoder passwordEncoder) {
        this.userGateway = userGateway;
        this.validateUserDataInputService = validateUserDataInputService;
        this.authenticateService = authenticateService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthenticationResponse change(ChangeUserViewModel userViewModel, User loggedInUser) throws InvalidDataException, UserAlreadyExistsException, EmailAlreadyExistsException, UsernameAlreadyExistsException {
        if (loggedInUser == null)
            throw new InvalidDataException();
        AuthenticationResponse authenticationResponse = null;
        try {
            userById = userGateway.getUserById(loggedInUser.getId());
            checkIfUsernameOrEmailAlreadyExists(userViewModel.username, userViewModel.email);
            changeUserToValuesFromViewModel(userViewModel);
            userGateway.saveUser(userById);
            authenticationResponse = authenticateService.authenticate(
                    new AuthenticationRequest(userById.getUsername(), userViewModel.password));
        } catch (UserDoesNotExistException e) {
            e.printStackTrace();
        }
        return authenticationResponse;
    }

    private void checkIfUsernameOrEmailAlreadyExists(String username, String email) throws EmailAlreadyExistsException, UserAlreadyExistsException, UsernameAlreadyExistsException {
        List<User> existingUsers = userGateway.getUserByUsernameOrEmail(username, email);
        if (existingUsers.size() > 0) {
            boolean usernameExists = false;
            boolean emailExists = false;
            for (User existingUser : existingUsers) {
                if (existingUser != userById) {
                    if (existingUser.getUsername().equals(username))
                        usernameExists = true;
                    if (existingUser.getEmail().equals(email))
                        emailExists = true;
                }
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

    private void changeUserToValuesFromViewModel(ChangeUserViewModel userViewModel) {
        if (userViewModel.username != null)
            userById.setUsername(userViewModel.username);
        if (userViewModel.nickName != null)
            userById.setNickName(userViewModel.nickName);
        if (userViewModel.email != null)
            userById.setEmail(userViewModel.email);
        if (userViewModel.userImage != null)
            userById.setPictureUrl(userViewModel.userImage);
        if (userViewModel.password != null)
            userById.setPassword(passwordEncoder.encode(userViewModel.password));
    }
}
