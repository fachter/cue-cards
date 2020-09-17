package com.project.cuecards.services;

import com.project.cuecards.entities.Folder;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.gateways.FolderGateway;
import com.project.cuecards.viewModels.DataViewModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersHomeDataServiceImpl implements UsersHomeDataService {

    private final FolderGateway folderGateway;
    private final PrepareDataViewModelService prepareDataViewModelService;

    public UsersHomeDataServiceImpl(FolderGateway folderGateway,
                                    PrepareDataViewModelService prepareDataViewModelService) {
        this.folderGateway = folderGateway;
        this.prepareDataViewModelService = prepareDataViewModelService;
    }

    @Override
    public DataViewModel get(User loggedInUser) {
        List<Folder> folders;
        try {
            folders = folderGateway.getRootFoldersByUser(loggedInUser);
        } catch (InvalidArgumentException e) {
            return new DataViewModel();
        }
        DataViewModel viewModel = prepareDataViewModelService.getFilledViewModel(folders, loggedInUser);
        viewModel.lastModified = loggedInUser.getLastModifiedDateTime();
        return viewModel;
    }
}
