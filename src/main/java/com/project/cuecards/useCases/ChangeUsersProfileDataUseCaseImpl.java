package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.ChangeUsersProfileDataUseCase;
import com.project.cuecards.entities.User;
import com.project.cuecards.viewModels.UserViewModel;
import org.springframework.stereotype.Service;

@Service
public class ChangeUsersProfileDataUseCaseImpl implements ChangeUsersProfileDataUseCase {

    @Override
    public void change(UserViewModel userViewModel, User loggedInUser) {

    }
}
