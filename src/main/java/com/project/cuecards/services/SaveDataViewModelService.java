package com.project.cuecards.services;

import com.project.cuecards.entities.Folder;
import com.project.cuecards.entities.Room;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.viewModels.DataViewModel;

import java.util.List;

public interface SaveDataViewModelService {

    void setRoom(Room room);

    void saveOrUpdateDataViewModel(DataViewModel viewModel,
                                   List<Folder> existingFolders,
                                   User loggedInUser) throws InvalidDataException;
}
