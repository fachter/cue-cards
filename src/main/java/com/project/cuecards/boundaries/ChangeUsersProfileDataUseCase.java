package com.project.cuecards.boundaries;

import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.EmailAlreadyExistsException;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.exceptions.UserAlreadyExistsException;
import com.project.cuecards.exceptions.UsernameAlreadyExistsException;
import com.project.cuecards.viewModels.AuthenticationResponse;
import com.project.cuecards.viewModels.ChangeUserViewModel;

public interface ChangeUsersProfileDataUseCase {

    AuthenticationResponse change(ChangeUserViewModel userViewModel, User loggedInUser) throws InvalidDataException, UserAlreadyExistsException, EmailAlreadyExistsException, UsernameAlreadyExistsException;
}
