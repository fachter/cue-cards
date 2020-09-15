package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.ChangeUsersProfileDataUseCase;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.exceptions.UserAlreadyExistsException;
import com.project.cuecards.exceptions.UserDoesNotExistException;
import com.project.cuecards.gateways.UserGateway;
import com.project.cuecards.viewModels.UserViewModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChangeUsersProfileDataUseCaseImpl implements ChangeUsersProfileDataUseCase {

    private final UserGateway userGateway;

    public ChangeUsersProfileDataUseCaseImpl(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public void change(UserViewModel userViewModel, User loggedInUser) throws InvalidDataException, UserAlreadyExistsException {
        if (loggedInUser == null)
            throw new InvalidDataException();
        try {
            User userById = userGateway.getUserById(loggedInUser.getId());
            List<User> usersWithViewModelUsernameOrEmail =
                    userGateway.getUserByUsernameOrEmail(userViewModel.username, userViewModel.email);
            for (User user : usersWithViewModelUsernameOrEmail) {
                if (user != userById)
                    throw new UserAlreadyExistsException();
            }
            userById.setFullName(userViewModel.fullName);
            userById.setEmail(userViewModel.email);
            userById.setPictureUrl(userViewModel.pictureUrl);
            userGateway.saveUser(userById);
        } catch (UserDoesNotExistException e) {
            e.printStackTrace();
        }

    }
}
