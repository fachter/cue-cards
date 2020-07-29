package com.project.cuecards.boundaries;

import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.exceptions.UserDoesNotExistException;
import com.project.cuecards.viewModels.DataViewModel;

public interface SaveUsersHomeData {

    void save(DataViewModel viewModel, String username) throws InvalidDataException, UserDoesNotExistException;

}
