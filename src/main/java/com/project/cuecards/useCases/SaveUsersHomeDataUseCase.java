package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.SaveUsersHomeData;
import com.project.cuecards.entities.*;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.exceptions.UserDoesNotExistException;
import com.project.cuecards.gateways.AnswerGateway;
import com.project.cuecards.gateways.CueCardGateway;
import com.project.cuecards.gateways.FolderGateway;
import com.project.cuecards.gateways.UserGateway;
import com.project.cuecards.services.SaveDataViewModelService;
import com.project.cuecards.viewModels.DataViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SaveUsersHomeDataUseCase implements SaveUsersHomeData {

    private final FolderGateway folderGateway;
    private final UserGateway userGateway;
    private final SaveDataViewModelService saveDataViewModelService;

    @Autowired
    public SaveUsersHomeDataUseCase(FolderGateway folderGateway,
                                    UserGateway userGateway,
                                    SaveDataViewModelService saveDataViewModelService) {
        this.folderGateway = folderGateway;
        this.userGateway = userGateway;
        this.saveDataViewModelService = saveDataViewModelService;
    }


    @Override
    public void save(DataViewModel viewModel, String username) throws InvalidDataException, UserDoesNotExistException {
        User user = userGateway.getUserByUsername(username);
        saveDataViewModelService.saveOrUpdateDataViewModel(viewModel, getExistingFolders(user), user);
        if (viewModel.lastModified != null) {
            user.setLastModifiedDateTime(viewModel.lastModified);
            userGateway.saveUser(user);
        }
    }

    private List<Folder> getExistingFolders(User user) {
        try {
            return folderGateway.getRootFoldersByUser(user);
        } catch (InvalidArgumentException e) {
            return new ArrayList<>();
        }
    }
}
