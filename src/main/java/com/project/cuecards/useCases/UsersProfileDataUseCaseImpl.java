package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.UsersProfileDataUseCase;
import com.project.cuecards.entities.User;
import com.project.cuecards.viewModels.UserViewModel;
import org.springframework.stereotype.Service;

@Service
public class UsersProfileDataUseCaseImpl implements UsersProfileDataUseCase {
    @Override
    public UserViewModel get(User loggedInUser) {
        UserViewModel userViewModel = new UserViewModel();
        userViewModel.fullName = "Test User";
        userViewModel.pictureUrl = "testUrl";
        return userViewModel;
    }
}
