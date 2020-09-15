package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.UsersProfileDataUseCase;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.viewModels.UserViewModel;
import org.springframework.stereotype.Service;

@Service
public class UsersProfileDataUseCaseImpl implements UsersProfileDataUseCase {
    @Override
    public UserViewModel get(User loggedInUser) throws InvalidDataException {
        if (loggedInUser == null)
            throw new InvalidDataException();
        UserViewModel userViewModel = new UserViewModel();
        userViewModel.fullName = loggedInUser.getFullName();
        userViewModel.pictureUrl = loggedInUser.getPictureUrl();
        userViewModel.email = loggedInUser.getEmail();
        return userViewModel;
    }
}
