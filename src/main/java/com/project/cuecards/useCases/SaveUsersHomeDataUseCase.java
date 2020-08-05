package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.SaveUsersHomeData;
import com.project.cuecards.entities.CueCard;
import com.project.cuecards.entities.Folder;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.exceptions.UserDoesNotExistException;
import com.project.cuecards.gateways.FolderGateway;
import com.project.cuecards.gateways.UserGateway;
import com.project.cuecards.viewModels.CueCardViewModel;
import com.project.cuecards.viewModels.DataViewModel;
import com.project.cuecards.viewModels.FolderViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class SaveUsersHomeDataUseCase implements SaveUsersHomeData {

    private final FolderGateway folderGateway;
    private final UserGateway userGateway;
    private ArrayList<Folder> foldersToPersist;
    private ArrayList<Folder> existingFolders;
    private User user;

    @Autowired
    public SaveUsersHomeDataUseCase(FolderGateway folderGateway,
                                    UserGateway userGateway) {
        this.folderGateway = folderGateway;
        this.userGateway = userGateway;
    }


    @Override
    public void save(DataViewModel viewModel, String username) throws InvalidDataException, UserDoesNotExistException {
        user = userGateway.getUserByUsername(username);
        foldersToPersist = new ArrayList<>();
        try {
            existingFolders = folderGateway.getRootFoldersByUser(user);
        } catch (InvalidArgumentException e) {
            existingFolders = new ArrayList<>();
        }
        addFoldersToList(viewModel.folders, null);
        try {
            if (foldersToPersist.size() > 0)
                folderGateway.addList(foldersToPersist);
        } catch (InvalidArgumentException e) {
            throw new InvalidDataException();
        }
    }

    private void addFoldersToList(ArrayList<FolderViewModel> folderViewModels, Folder rootFolder) {
        for (FolderViewModel folderViewModel : folderViewModels) {
            if (folderViewModel.ID == 0) {
                Folder folder = getFolderFromViewModel(folderViewModel, rootFolder);
                foldersToPersist.add(folder);
                addFoldersToList(folderViewModel.subFolders, folder);
                addCueCardsToSet(folderViewModel, folder);
            } else {
                Folder existingFolder = getFolderById(folderViewModel, rootFolder);
                existingFolder.setName(folderViewModel.name);
                foldersToPersist.add(existingFolder);
            }
        }
    }

    private Folder getFolderById(FolderViewModel folderViewModel, Folder rootFolder) {
        for (Folder folder : existingFolders) {
            if (folder.getId().equals(folderViewModel.ID))
                return folder;
        }
        return getFolderFromViewModel(folderViewModel, rootFolder);
    }

    private Folder getFolderFromViewModel(FolderViewModel folderViewModel, Folder rootFolder) {
        Folder folder = new Folder();
        folder.setSet(!folderViewModel.isFolder);
        folder.setName(folderViewModel.name);
        folder.setRootFolder(rootFolder);
        folder.setCreatedBy(user);
        return folder;
    }

    private void addCueCardsToSet(FolderViewModel setViewModel, Folder set) {
        for (CueCardViewModel cardViewModel : setViewModel.cueCards) {
            CueCard cueCard = getCueCardFromViewModel(set, cardViewModel);
            set.getCueCards().add(cueCard);
        }
    }

    private CueCard getCueCardFromViewModel(Folder set, CueCardViewModel cardViewModel) {
        CueCard cueCard = new CueCard();
        cueCard.setQuestion(cardViewModel.questionText);
        cueCard.setAnswer(cardViewModel.answer);
        cueCard.setTopic(cardViewModel.cardTopic);
        cueCard.setCreatedBy(user);
        cueCard.setSet(set);
        return cueCard;
    }

}
