package com.project.cuecards.boundaries;

import com.project.cuecards.entities.User;
import com.project.cuecards.viewModels.UserViewModel;

public interface ChangeUsersProfileDataUseCase {

    void change(UserViewModel userViewModel, User loggedInUser);
}
