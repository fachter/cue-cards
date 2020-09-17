package com.project.cuecards.services;

import com.project.cuecards.entities.User;
import com.project.cuecards.viewModels.DataViewModel;

public interface UsersHomeDataService {
    DataViewModel get(User loggedInUser);
}
