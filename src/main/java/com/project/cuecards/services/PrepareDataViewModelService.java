package com.project.cuecards.services;

import com.project.cuecards.entities.Folder;
import com.project.cuecards.entities.User;
import com.project.cuecards.viewModels.DataViewModel;

import java.util.List;

public interface PrepareDataViewModelService {

    DataViewModel getFilledViewModel(List<Folder> folders, User loggedInUser);
}
