package com.project.cuecards.services;

import com.project.cuecards.exceptions.EmailAlreadyExistsException;
import com.project.cuecards.exceptions.UserAlreadyExistsException;
import com.project.cuecards.exceptions.UsernameAlreadyExistsException;

public interface ValidateUserDataInputService {
    void checkIfUsernameOrEmailAlreadyExists(String username, String email) throws EmailAlreadyExistsException, UserAlreadyExistsException, UsernameAlreadyExistsException;
}
