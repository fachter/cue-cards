package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.GetUsersHomeData;
import com.project.cuecards.entities.*;
import com.project.cuecards.exceptions.UserDoesNotExistException;
import com.project.cuecards.gateways.UserGateway;
import com.project.cuecards.services.UsersHomeDataService;
import com.project.cuecards.viewModels.DataViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetUsersHomeDataUseCase implements GetUsersHomeData {

    private final UserGateway userGateway;
    private final UsersHomeDataService usersHomeDataService;

    @Autowired
    public GetUsersHomeDataUseCase(UserGateway userGateway,
                                   UsersHomeDataService usersHomeDataService) {
        this.userGateway = userGateway;
        this.usersHomeDataService = usersHomeDataService;
    }

    @Override
    public DataViewModel get(String username) throws UserDoesNotExistException {
        User user = userGateway.getUserByUsername(username);
        return usersHomeDataService.get(user);
    }

}
