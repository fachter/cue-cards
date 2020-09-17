package com.project.cuecards.services;

import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.viewModels.UserViewModel;
import org.springframework.stereotype.Service;

@Service
public class UsersProfileDataServiceImpl implements UsersProfileDataService {
    @Override
    public UserViewModel get(User loggedInUser) throws InvalidDataException {
        if (loggedInUser == null)
            throw new InvalidDataException();
        return getUserViewModel(loggedInUser);
    }

    private UserViewModel getUserViewModel(User loggedInUser) {
        UserViewModel userViewModel = new UserViewModel();
        userViewModel.username = loggedInUser.getUsername();
        userViewModel.fullName = loggedInUser.getFullName();
        userViewModel.userImage = loggedInUser.getPictureUrl();
        userViewModel.email = loggedInUser.getEmail();
        return userViewModel;
    }
}
