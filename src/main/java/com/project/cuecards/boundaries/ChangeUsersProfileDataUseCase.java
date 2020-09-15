package com.project.cuecards.boundaries;

import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.exceptions.UserAlreadyExistsException;
import com.project.cuecards.viewModels.UserViewModel;

public interface ChangeUsersProfileDataUseCase {

    void change(UserViewModel userViewModel, User loggedInUser) throws InvalidDataException, UserAlreadyExistsException;
}
