package com.project.cuecards.services;

import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.viewModels.UserViewModel;

public interface UsersProfileDataService {
    UserViewModel get(User loggedInUser) throws InvalidDataException;

    UserViewModel getUserViewModelFromUser(User loggedInUser);
}
