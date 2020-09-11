package com.project.cuecards.services;

import com.project.cuecards.entities.*;
import com.project.cuecards.viewModels.AnswerViewModel;
import com.project.cuecards.viewModels.CueCardViewModel;
import com.project.cuecards.viewModels.DataViewModel;
import com.project.cuecards.viewModels.FolderViewModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PrepareDataViewModelServiceImpl implements PrepareDataViewModelService {

    private User loggedInUser;

    @Override
    public DataViewModel getFilledViewModel(List<Folder> folders, User loggedInUser) {
        this.loggedInUser = loggedInUser;
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
        setViewModel.id = f.getUid();
        setViewModel.name = f.getName();
        for (CueCard c : f.getCueCards())
            setViewModel.cards.add(getCueCardViewModel(c));

        return setViewModel;
    }

    private CueCardViewModel getCueCardViewModel(CueCard c) {
        CueCardViewModel cardViewModel = new CueCardViewModel();
        cardViewModel.id = c.getUid();
        cardViewModel.cardTopic = c.getTopic();
        cardViewModel.questionText = c.getQuestion();
        cardViewModel.solution = c.getSolution();
        cardViewModel.cardType = c.getCardType();
        cardViewModel.cardLevel = getUsersCardLevel(c);
        cardViewModel.answers = getAnswerViewModels(c.getAnswers());
        return cardViewModel;
    }

    private int getUsersCardLevel(CueCard c) {
        for (CardLevel cardLevel : c.getCardLevels()) {
            if (cardLevel.getUser().equals(loggedInUser))
                return cardLevel.getUsersCardLevel();
        }
        return 0;
    }

    private ArrayList<AnswerViewModel> getAnswerViewModels(List<Answer> answers) {
        ArrayList<AnswerViewModel> answerViewModels = new ArrayList<>();
        for (Answer answer : answers) {
            AnswerViewModel answerViewModel = new AnswerViewModel();
            answerViewModel.id = answer.getUid();
            answerViewModel.text = answer.getText();
            answerViewModels.add(answerViewModel);
        }
        return answerViewModels;
    }

    private FolderViewModel getFolderViewModel(Folder f) {
        FolderViewModel folderViewModel = new FolderViewModel();
        folderViewModel.id = f.getUid();
        folderViewModel.name = f.getName();
        List<Folder> folders = f.getSubFolders();
        addFolderAndSetViewModelsFromList(folders, folderViewModel.subFolders);
        return folderViewModel;
    }
}
