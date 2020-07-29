package com.project.cuecards.boundaries;

import com.project.cuecards.exceptions.UserDoesNotExistException;
import com.project.cuecards.viewModels.DataViewModel;

public interface GetUsersHomeData {

    DataViewModel get(String username) throws UserDoesNotExistException;

}
