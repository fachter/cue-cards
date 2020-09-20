package com.project.cuecards.boundaries;

import com.project.cuecards.exceptions.EmailAlreadyExistsException;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.exceptions.UserAlreadyExistsException;
import com.project.cuecards.exceptions.UsernameAlreadyExistsException;
import com.project.cuecards.viewModels.RegisterRequest;

public interface CreateNewUser {

    void create(RegisterRequest registerRequest) throws UserAlreadyExistsException, InvalidDataException, EmailAlreadyExistsException, UsernameAlreadyExistsException;
}
