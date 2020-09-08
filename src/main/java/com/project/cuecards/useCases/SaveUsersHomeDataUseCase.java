package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.SaveUsersHomeData;
import com.project.cuecards.entities.*;
import com.project.cuecards.exceptions.CardLevelDoesNotExistException;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.exceptions.UserDoesNotExistException;
import com.project.cuecards.gateways.AnswerGateway;
import com.project.cuecards.gateways.CueCardGateway;
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
    private final CueCardGateway cueCardGateway;
    private final AnswerGateway answerGateway;
    private ArrayList<Folder> foldersToPersist;
    private ArrayList<Folder> existingFolders;
    private ArrayList<CueCard> cardsToRemove;
    private User user;
    private ArrayList<Answer> answersToRemove;

    @Autowired
    public SaveUsersHomeDataUseCase(FolderGateway folderGateway,
                                    UserGateway userGateway,
                                    CueCardGateway cueCardGateway,
                                    AnswerGateway answerGateway) {
        this.folderGateway = folderGateway;
        this.userGateway = userGateway;
        this.cueCardGateway = cueCardGateway;
        this.answerGateway = answerGateway;
    }


    @Override
    public void save(DataViewModel viewModel, String username) throws InvalidDataException, UserDoesNotExistException {
        user = userGateway.getUserByUsername(username);
        foldersToPersist = new ArrayList<>();
        cardsToRemove = new ArrayList<>();
        answersToRemove = new ArrayList<>();
        try {
            existingFolders = folderGateway.getRootFoldersByUser(user);
        } catch (InvalidArgumentException e) {
            existingFolders = new ArrayList<>();
        }
        addFoldersToList(viewModel.folders);
        persistToDb();
        if (viewModel.lastModified != null) {
            user.setLastModifiedDateTime(viewModel.lastModified);
            userGateway.saveUser(user);
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
        folder.setCreatedBy(user);
        return folder;
    }

    private void addCueCardsToSet(FolderViewModel setViewModel, Folder set) {
        ArrayList<CueCard> cards = new ArrayList<>();
        for (CueCardViewModel cardViewModel : setViewModel.cards) {
            CueCard cueCard = getCueCardFromViewModel(set, cardViewModel);
            cards.add(cueCard);
        }
        for (CueCard cueCard : set.getCueCards()) {
            if (!cards.contains(cueCard))
                cardsToRemove.add(cueCard);
        }
        set.setCueCards(cards);
    }

    private CueCard getCueCardFromViewModel(Folder set, CueCardViewModel cardViewModel) {
        CueCard cueCard = getCard(set, cardViewModel)
                .setQuestion(cardViewModel.questionText)
                .setSolution(cardViewModel.solution)
                .setTopic(cardViewModel.cardTopic)
                .setCardType(cardViewModel.cardType)
                .setSet(set);
        cueCard.setCardLevels(getCardLevelsForCueCard(cardViewModel, cueCard))
                .setAnswers(getAnswersFromViewModel(cardViewModel.answers, cueCard))
                .setUid(cardViewModel.id);
        cueCard.setCreatedBy(user);
        return cueCard;
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
        cardLevel.setCueCard(cueCard).setUser(user).setUsersCardLevel(cardViewModel.cardLevel);
        return cardLevels;
    }

    private CardLevel getCardLevel(List<CardLevel> cardLevels) throws CardLevelDoesNotExistException {
        for (CardLevel cardLevel : cardLevels) {
            if (cardLevel.getUser().equals(user))
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
                folderGateway.addList(foldersToPersist);
            removeFolders();
            removeCueCards();
            removeAnswers();
        } catch (InvalidArgumentException e) {
            throw new InvalidDataException();
        }
    }

    private void removeFolders() throws InvalidArgumentException {
        ArrayList<Folder> foldersToRemove = new ArrayList<>();
        for (Folder existingFolder : existingFolders) {
            if (!foldersToPersist.contains(existingFolder))
                foldersToRemove.add(existingFolder);
        }
        if (foldersToRemove.size() > 0)
            folderGateway.removeList(foldersToRemove);
    }

    private void removeCueCards() throws InvalidArgumentException {
        if (cardsToRemove.size() > 0)
            cueCardGateway.removeList(cardsToRemove);
    }

    private void removeAnswers() throws InvalidArgumentException {
        if (answersToRemove.size() > 0)
            answerGateway.removeList(answersToRemove);
    }
}
