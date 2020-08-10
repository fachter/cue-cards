package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.GetUsersHomeData;
import com.project.cuecards.entities.Answer;
import com.project.cuecards.entities.CueCard;
import com.project.cuecards.entities.Folder;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.exceptions.UserDoesNotExistException;
import com.project.cuecards.gateways.FolderGateway;
import com.project.cuecards.gateways.UserGateway;
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

    @Autowired
    public GetUsersHomeDataUseCase(UserGateway userGateway, FolderGateway folderGateway) {
        this.userGateway = userGateway;
        this.folderGateway = folderGateway;
    }

    @Override
    public DataViewModel get(String username) throws UserDoesNotExistException {
        User user = userGateway.getUserByUsername(username);
        ArrayList<Folder> folders;
        try {
            folders = folderGateway.getRootFoldersByUser(user);
        } catch (InvalidArgumentException e) {
            return new DataViewModel();
        }
        DataViewModel viewModel = new DataViewModel();
        addFolderAndSetViewModelsFromList(folders, viewModel.folders);
        return viewModel;
    }

    private void addFolderAndSetViewModelsFromList(List<Folder> folders,
                                                   ArrayList<FolderViewModel> subFolders) {
        ArrayList<FolderViewModel> sets = new ArrayList<>();
        for (Folder folder : folders) {
            if (folder.isSet())
                sets.add(getSetViewModel(folder));
            else
                subFolders.add(getFolderViewModel(folder));
        }
        subFolders.addAll(sets);
    }

    private FolderViewModel getSetViewModel(Folder f) {
        FolderViewModel setViewModel = new FolderViewModel();
        setViewModel.isFolder = false;
        setViewModel.ID = f.getUid();
        setViewModel.name = f.getName();
        for (CueCard c : f.getCueCards())
            setViewModel.cards.add(getCueCardViewModel(c));

        return setViewModel;
    }

    private CueCardViewModel getCueCardViewModel(CueCard c) {
        CueCardViewModel cardViewModel = new CueCardViewModel();
        cardViewModel.cardID = c.getUid();
        cardViewModel.cardTopic = c.getTopic();
        cardViewModel.questionText = c.getQuestion();
        cardViewModel.solution = c.getSolution();
        cardViewModel.cardType = c.getCardType();
        cardViewModel.answers = getAnswerViewModels(c.getAnswers());
        return cardViewModel;
    }

    private ArrayList<AnswerViewModel> getAnswerViewModels(List<Answer> answers) {
        ArrayList<AnswerViewModel> answerViewModels = new ArrayList<>();
        for (Answer answer : answers) {
            AnswerViewModel answerViewModel = new AnswerViewModel();
            answerViewModel.ID = answer.getUid();
            answerViewModel.text = answer.getText();
            answerViewModels.add(answerViewModel);
        }
        return answerViewModels;
    }

    private FolderViewModel getFolderViewModel(Folder f) {
        FolderViewModel folderViewModel = new FolderViewModel();
        folderViewModel.ID = f.getUid();
        folderViewModel.name = f.getName();
        List<Folder> folders = f.getSubFolders();
        addFolderAndSetViewModelsFromList(folders, folderViewModel.subFolders);
        return folderViewModel;
    }

}
