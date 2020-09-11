package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.GetUsersHomeData;
import com.project.cuecards.entities.*;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.exceptions.UserDoesNotExistException;
import com.project.cuecards.gateways.FolderGateway;
import com.project.cuecards.gateways.UserGateway;
import com.project.cuecards.services.PrepareDataViewModelService;
import com.project.cuecards.viewModels.AnswerViewModel;
import com.project.cuecards.viewModels.CueCardViewModel;
import com.project.cuecards.viewModels.DataViewModel;
import com.project.cuecards.viewModels.FolderViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetUsersHomeDataUseCase implements GetUsersHomeData {

    private final UserGateway userGateway;
    private final FolderGateway folderGateway;
    private PrepareDataViewModelService prepareDataViewModelService;
    private User user;

    @Autowired
    public GetUsersHomeDataUseCase(UserGateway userGateway,
                                   FolderGateway folderGateway,
                                   PrepareDataViewModelService prepareDataViewModelService) {
        this.userGateway = userGateway;
        this.folderGateway = folderGateway;
        this.prepareDataViewModelService = prepareDataViewModelService;
    }

    @Override
    public DataViewModel get(String username) throws UserDoesNotExistException {
        user = userGateway.getUserByUsername(username);
        List<Folder> folders;
        try {
            folders = folderGateway.getRootFoldersByUser(user);
        } catch (InvalidArgumentException e) {
            return new DataViewModel();
        }
        DataViewModel viewModel = prepareDataViewModelService.getFilledViewModel(folders, user);
        viewModel.lastModified = user.getLastModifiedDateTime();
        return viewModel;
    }

}
