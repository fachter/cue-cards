package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.UsersProfileDataUseCase;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.services.UsersProfileDataService;
import com.project.cuecards.viewModels.UserViewModel;
import org.springframework.stereotype.Service;

@Service
public class UsersProfileDataUseCaseImpl implements UsersProfileDataUseCase {
    private final UsersProfileDataService usersProfileDataService;

    public UsersProfileDataUseCaseImpl(UsersProfileDataService usersProfileDataService) {
        this.usersProfileDataService = usersProfileDataService;
    }

    @Override
    public UserViewModel get(User loggedInUser) throws InvalidDataException {
        return usersProfileDataService.get(loggedInUser);
    }
}
