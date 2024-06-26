package com.project.cuecards.services;

import com.project.cuecards.entities.*;
import com.project.cuecards.exceptions.CardLevelDoesNotExistException;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.gateways.AnswerGateway;
import com.project.cuecards.gateways.CueCardGateway;
import com.project.cuecards.gateways.FolderGateway;
import com.project.cuecards.viewModels.AnswerViewModel;
import com.project.cuecards.viewModels.CueCardViewModel;
import com.project.cuecards.viewModels.DataViewModel;
import com.project.cuecards.viewModels.FolderViewModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class SaveDataViewModelServiceImpl implements SaveDataViewModelService {

    private final FolderGateway folderGateway;
    private final CueCardGateway cueCardGateway;
    private final AnswerGateway answerGateway;
    private List<Folder> foldersToPersist;
    private List<Folder> existingFolders;
    private List<Folder> foldersToRemove;
    private List<CueCard> cardsToRemove;
    private List<Answer> answersToRemove;
    private User loggedInUser;
    private Room room;

    public SaveDataViewModelServiceImpl(FolderGateway folderGateway, CueCardGateway cueCardGateway, AnswerGateway answerGateway) {
        this.folderGateway = folderGateway;
        this.cueCardGateway = cueCardGateway;
        this.answerGateway = answerGateway;
    }

    @Override
    public void setRoom(Room room) {
        this.room = room;
    }

    @Override
    public void saveOrUpdateDataViewModel(DataViewModel viewModel,
                                          List<Folder> existingFolders,
                                          User loggedInUser) throws InvalidDataException {
        this.loggedInUser = loggedInUser;
        this.existingFolders = existingFolders;
        initEmptyLists();
        addFoldersToList(viewModel.folders);
        persistToDb();
    }

    private void initEmptyLists() {
        foldersToPersist = new ArrayList<>();
        foldersToRemove = new ArrayList<>();
        cardsToRemove = new ArrayList<>();
        answersToRemove = new ArrayList<>();
    }

    private void addFoldersToList(ArrayList<FolderViewModel> folderViewModels) {
        for (FolderViewModel folderViewModel : folderViewModels) {
            Folder folder = getRootFolderWithRoom(folderViewModel);
            addCueCardsToSet(folderViewModel, folder);
            addSubFolders(folderViewModel.subFolders, folder);
            foldersToPersist.add(folder);
        }
    }

    private void addSubFolders(List<FolderViewModel> folderViewModels, Folder rootFolder) {
        List<Folder> newSubFolders = getSubFoldersForRootFolder(folderViewModels, rootFolder);
//        addAllSubFoldersToRemove(rootFolder.getSubFolders(), newSubFolders);
        List<Folder> subFoldersToRemove = new ArrayList<>();
        for (Folder subFolder : rootFolder.getSubFolders()) {
            if (!newSubFolders.contains(subFolder))
                subFoldersToRemove.add(subFolder);
        }
        rootFolder.getSubFolders().removeAll(subFoldersToRemove);
        List<Folder> subFoldersToAdd = new ArrayList<>();
        for (Folder subFolder : newSubFolders) {
            if (!rootFolder.getSubFolders().contains(subFolder))
                subFoldersToAdd.add(subFolder);
        }
        rootFolder.getSubFolders().addAll(subFoldersToAdd);
    }

    private void addAllSubFoldersToRemove(List<Folder> existingSubFolders, List<Folder> subFolders) {
        for (Folder existingSubFolder : existingSubFolders)
            if (!subFolders.contains(existingSubFolder))
                foldersToRemove.add(existingSubFolder);
    }

    private List<Folder> getSubFoldersForRootFolder(List<FolderViewModel> folderViewModels, Folder rootFolder) {
        List<Folder> subFolders = new ArrayList<>();
        for (FolderViewModel folderViewModel : folderViewModels) {
            Folder folder = getFolderByIdOrNewFolder(folderViewModel, rootFolder);
            addCueCardsToSet(folderViewModel, folder);
            addSubFolders(folderViewModel.subFolders, folder);
            subFolders.add(folder);
        }
        return subFolders;
    }

    private Folder getRootFolderWithRoom(FolderViewModel folderViewModel) {
        return getFolderByIdOrNewFolder(folderViewModel, null).setRoom(room);
    }

    private Folder getFolderByIdOrNewFolder(FolderViewModel folderViewModel, Folder rootFolder) {
        for (Folder folder : existingFolders) {
            if (folder.getUid().equals(folderViewModel.id))
                return folder.setName(folderViewModel.name);
        }
        if (rootFolder != null)
            for (Folder subFolder : rootFolder.getSubFolders())
                if (subFolder.getUid().equals(folderViewModel.id))
                    return subFolder.setName(folderViewModel.name);
        return getFolderFromViewModel(folderViewModel, rootFolder);
    }

    private Folder getFolderFromViewModel(FolderViewModel folderViewModel, Folder rootFolder) {
        Folder folder = new Folder()
                .setSet(!folderViewModel.isFolder)
                .setName(folderViewModel.name)
                .setRootFolder(rootFolder);
        folder.setUid(folderViewModel.id);
        folder.setCreatedBy(loggedInUser);
        return folder;
    }

    private void addCueCardsToSet(FolderViewModel setViewModel, Folder set) {
        ArrayList<CueCard> newCards = new ArrayList<>();
        for (CueCardViewModel cardViewModel : setViewModel.cards) {
            CueCard cueCard = getCueCardFromViewModel(set, cardViewModel);
            newCards.add(cueCard);
        }

        removeCardsFromSet(set, newCards);
        addMissingCardsToSet(set, newCards);
    }

    private void removeCardsFromSet(Folder set, ArrayList<CueCard> newCards) {
        List<CueCard> cardsToDelete = new ArrayList<>();
        for (CueCard cueCard : set.getCueCards()) {
            if (!newCards.contains(cueCard))
                cardsToDelete.add(cueCard);
        }
        set.getCueCards().removeAll(cardsToDelete);
    }

    private void addMissingCardsToSet(Folder set, ArrayList<CueCard> newCards) {
        List<CueCard> cardsToAdd = new ArrayList<>();
        for (CueCard cueCard : newCards) {
            if (!set.getCueCards().contains(cueCard))
                cardsToAdd.add(cueCard);
        }
        set.getCueCards().addAll(cardsToAdd);
    }

    private CueCard getCueCardFromViewModel(Folder set, CueCardViewModel cardViewModel) {
        CueCard cueCard = getCard(set, cardViewModel)
                .setQuestion(cardViewModel.questionText)
                .setSolution(cardViewModel.solution)
                .setTopic(cardViewModel.cardTopic)
                .setCardType(cardViewModel.cardType)
                .setSet(set);
        cueCard.setCardLevels(getCardLevelsForCueCard(cardViewModel, cueCard))
                .setUid(cardViewModel.id);
        List<Answer> newAnswers = getAnswersFromViewModel(cardViewModel.answers, cueCard);
        removeAnswers(cueCard, newAnswers);
        addMissingAnswers(cueCard, newAnswers);
        cueCard.setCreatedBy(loggedInUser);
        return cueCard;
    }

    private void addMissingAnswers(CueCard cueCard, List<Answer> newAnswers) {
        List<Answer> answersToAdd = new ArrayList<>();
        for (Answer answer : newAnswers) {
            if (!cueCard.getAnswers().contains(answer))
                answersToAdd.add(answer);
        }
        cueCard.getAnswers().addAll(answersToAdd);
    }

    private void removeAnswers(CueCard cueCard, List<Answer> newAnswers) {
        List<Answer> answersToRemove = new ArrayList<>();
        for (Answer answer : cueCard.getAnswers()) {
            if (!newAnswers.contains(answer))
                answersToRemove.add(answer);
        }
        cueCard.getAnswers().removeAll(answersToRemove);
    }

    private List<CardLevel> getCardLevelsForCueCard(CueCardViewModel cardViewModel, CueCard cueCard) {
        List<CardLevel> cardLevels = cueCard.getCardLevels();
        CardLevel cardLevel;
        try {
            cardLevel = getCardLevel(cardLevels);
        } catch (CardLevelDoesNotExistException e) {
            cardLevel = new CardLevel();
            cardLevels.add(cardLevel);
        }
        cardLevel.setCueCard(cueCard).setUser(loggedInUser).setUsersCardLevel(cardViewModel.cardLevel);
        return cardLevels;
    }

    private CardLevel getCardLevel(List<CardLevel> cardLevels) throws CardLevelDoesNotExistException {
        for (CardLevel cardLevel : cardLevels) {
            if (cardLevel.getUser().equals(loggedInUser))
                return cardLevel;
        }
        throw new CardLevelDoesNotExistException();
    }

    private CueCard getCard(Folder set, CueCardViewModel cardViewModel) {
        for (CueCard card : set.getCueCards()) {
            if (card.getUid().equals(cardViewModel.id))
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
            answer.setUid(answerViewModel.id);
            answers.add(answer);
        }
        for (Answer answer : cueCard.getAnswers()) {
            if (!answers.contains(answer))
                answersToRemove.add(answer);
        }
        return answers;
    }

    private Answer getAnswer(CueCard cueCard, AnswerViewModel answerViewModel) {
        for (Answer answer : cueCard.getAnswers()) {
            if (answer.getUid().equals(answerViewModel.id))
                return answer;
        }
        return new Answer();
    }

    private void persistToDb() throws InvalidDataException {
        try {
            if (foldersToPersist.size() > 0)
                folderGateway.saveList(foldersToPersist);
            removeFolders();
        } catch (InvalidArgumentException e) {
            throw new InvalidDataException();
        }
    }

    private void removeFolders() throws InvalidArgumentException {
        for (Folder existingFolder : existingFolders) {
            if (!foldersToPersist.contains(existingFolder))
                foldersToRemove.add(existingFolder);
        }
        if (foldersToRemove.size() > 0)
            folderGateway.removeList(foldersToRemove);
        existingFolders.removeAll(foldersToRemove);
    }
}
