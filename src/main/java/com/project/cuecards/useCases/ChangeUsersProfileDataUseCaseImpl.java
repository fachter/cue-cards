package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.ChangeUsersProfileDataUseCase;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.exceptions.UserAlreadyExistsException;
import com.project.cuecards.exceptions.UserDoesNotExistException;
import com.project.cuecards.gateways.UserGateway;
import com.project.cuecards.services.AuthenticateService;
import com.project.cuecards.viewModels.AuthenticationRequest;
import com.project.cuecards.viewModels.AuthenticationResponse;
import com.project.cuecards.viewModels.ChangeUserViewModel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChangeUsersProfileDataUseCaseImpl implements ChangeUsersProfileDataUseCase {

    private final UserGateway userGateway;
    private final AuthenticateService authenticateService;
    private final PasswordEncoder passwordEncoder;

    public ChangeUsersProfileDataUseCaseImpl(UserGateway userGateway,
                                             AuthenticateService authenticateService,
                                             PasswordEncoder passwordEncoder) {
        this.userGateway = userGateway;
        this.authenticateService = authenticateService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthenticationResponse change(ChangeUserViewModel userViewModel, User loggedInUser) throws InvalidDataException, UserAlreadyExistsException {
        if (loggedInUser == null)
            throw new InvalidDataException();
        AuthenticationResponse authenticationResponse = null;
        try {
            User userById = getUserById(userViewModel, loggedInUser);
            changeUserToValuesFromViewModel(userById, userViewModel);
            userGateway.saveUser(userById);
            authenticationResponse = authenticateService.authenticate(
                    new AuthenticationRequest(userById.getUsername(), userViewModel.password));
        } catch (UserDoesNotExistException e) {
            e.printStackTrace();
        }
        return authenticationResponse;
    }

    private User getUserById(ChangeUserViewModel userViewModel, User loggedInUser) throws UserDoesNotExistException, UserAlreadyExistsException {
        User userById = userGateway.getUserById(loggedInUser.getId());
        List<User> usersWithViewModelUsernameOrEmail =
                userGateway.getUserByUsernameOrEmail(userViewModel.username, userViewModel.email);
        for (User user : usersWithViewModelUsernameOrEmail) {
            if (user != userById)
                throw new UserAlreadyExistsException();
        }
        return userById;
    }

    private void changeUserToValuesFromViewModel(User userById, ChangeUserViewModel userViewModel) {
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
