package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.SaveUsersHomeData;
import com.project.cuecards.entities.Answer;
import com.project.cuecards.entities.CueCard;
import com.project.cuecards.entities.Folder;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.exceptions.InvalidDataException;
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
        addFoldersToList(viewModel.folders);
        try {
            if (foldersToPersist.size() > 0)
                folderGateway.addList(foldersToPersist);
        } catch (InvalidArgumentException e) {
            throw new InvalidDataException();
        }
    }

    private void addFoldersToList(ArrayList<FolderViewModel> folderViewModels) {
        for (FolderViewModel folderViewModel : folderViewModels) {
            Folder folder = getFolderByIdOrNewFolder(folderViewModel, null);
            addCueCardsToSet(folderViewModel, folder);
            addSubFolders(folderViewModel.subFolders, folder);
            foldersToPersist.add(folder);
        }
    }

    private void addSubFolders(ArrayList<FolderViewModel> folderViewModels, Folder rootFolder) {
        ArrayList<Folder> subFolders = new ArrayList<>();
        for (FolderViewModel folderViewModel : folderViewModels) {
            Folder folder = getFolderByIdOrNewFolder(folderViewModel, rootFolder);
            addCueCardsToSet(folderViewModel, folder);
            addSubFolders(folderViewModel.subFolders, folder);
            subFolders.add(folder);
        }
        rootFolder.setSubFolders(subFolders);
    }

    private Folder getFolderByIdOrNewFolder(FolderViewModel folderViewModel, Folder rootFolder) {
        for (Folder folder : existingFolders) {
            if (folder.getUid().equals(folderViewModel.ID))
                return folder.setName(folderViewModel.name);
        }
        if (rootFolder != null)
            for (Folder subFolder : rootFolder.getSubFolders())
                if (subFolder.getUid().equals(folderViewModel.ID))
                    return subFolder.setName(folderViewModel.name);
        return getFolderFromViewModel(folderViewModel, rootFolder);
    }

    private Folder getFolderFromViewModel(FolderViewModel folderViewModel, Folder rootFolder) {
        Folder folder = new Folder()
                .setSet(!folderViewModel.isFolder)
                .setName(folderViewModel.name)
                .setRootFolder(rootFolder);
        folder.setUid(folderViewModel.ID);
        folder.setCreatedBy(user);
        return folder;
    }

    private void addCueCardsToSet(FolderViewModel setViewModel, Folder set) {
        for (CueCardViewModel cardViewModel : setViewModel.cards) {
            CueCard cueCard = getCueCardFromViewModel(set, cardViewModel);
            set.getCueCards().add(cueCard);
        }
    }

    private CueCard getCueCardFromViewModel(Folder set, CueCardViewModel cardViewModel) {
        CueCard cueCard = getCard(set, cardViewModel)
                .setQuestion(cardViewModel.questionText)
                .setSolution(cardViewModel.solution)
                .setTopic(cardViewModel.cardTopic)
                .setLevel(cardViewModel.cardLevel)
                .setCardType(cardViewModel.cardType)
                .setSet(set);
        cueCard.setAnswers(getAnswersFromViewModel(cardViewModel.answers, cueCard))
                .setUid(cardViewModel.cardID);
        cueCard.setCreatedBy(user);
        return cueCard;
    }

    private CueCard getCard(Folder set, CueCardViewModel cardViewModel) {
        for (CueCard card : set.getCueCards()) {
            if (card.getUid().equals(cardViewModel.cardID))
                return card;
        }
        return new CueCard();
    }

    private List<Answer> getAnswersFromViewModel(ArrayList<AnswerViewModel> answerViewModels, CueCard cueCard) {
        ArrayList<Answer> answers = new ArrayList<>();
        for (AnswerViewModel answerViewModel : answerViewModels) {
            Answer answer = getAnswer(cueCard, answerViewModel)
                    .setCueCard(cueCard)
                    .setText(answerViewModel.text);
            answer.setUid(answerViewModel.ID);
            answers.add(answer);
        }
        return answers;
    }

    private Answer getAnswer(CueCard cueCard, AnswerViewModel answerViewModel) {
        for (Answer answer : cueCard.getAnswers()) {
            if (answer.getUid().equals(answerViewModel.ID))
                return answer;
        }
        return new Answer();
    }

}
