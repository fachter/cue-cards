package com.project.cuecards.boundaries;

import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.viewModels.UserViewModel;

public interface UsersProfileDataUseCase {

    UserViewModel get(User loggedInUser) throws InvalidDataException;
}
