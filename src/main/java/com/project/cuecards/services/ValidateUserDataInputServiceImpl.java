package com.project.cuecards.services;

import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.EmailAlreadyExistsException;
import com.project.cuecards.exceptions.UserAlreadyExistsException;
import com.project.cuecards.exceptions.UsernameAlreadyExistsException;
import com.project.cuecards.gateways.UserGateway;
import com.project.cuecards.viewModels.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidateUserDataInputServiceImpl implements ValidateUserDataInputService {
    private final UserGateway userGateway;

    @Autowired
    public ValidateUserDataInputServiceImpl(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public void checkIfUsernameOrEmailAlreadyExists(String username, String email) throws EmailAlreadyExistsException, UserAlreadyExistsException, UsernameAlreadyExistsException {
//        List<User> existingUsers = userGateway.getUserByUsernameOrEmail(username, email);
//        if (existingUsers.size() > 0) {
//            boolean usernameExists = false;
//            boolean emailExists = false;
//            for (User existingUser : existingUsers) {
//                if (existingUser.getUsername().equals(username))
//                    usernameExists = true;
//                if (existingUser.getEmail().equals(email))
//                    emailExists = true;
//            }
//            //noinspection ConstantConditions
//            throwProperException(usernameExists, emailExists);
//        }
    }

    private void throwProperException(boolean usernameExists, boolean emailExists) throws UsernameAlreadyExistsException, EmailAlreadyExistsException, UserAlreadyExistsException {
        if (usernameExists && !emailExists)
            throw new UsernameAlreadyExistsException();
        else if (emailExists && !usernameExists)
            throw new EmailAlreadyExistsException();
        else
            throw new UserAlreadyExistsException();
    }
}
