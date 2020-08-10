package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.GetUsersHomeData;
import com.project.cuecards.entities.Answer;
import com.project.cuecards.entities.CueCard;
import com.project.cuecards.entities.Folder;
import com.project.cuecards.entities.User;
import com.project.cuecards.enums.CardType;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.exceptions.UserDoesNotExistException;
import com.project.cuecards.gateways.FolderGateway;
import com.project.cuecards.gateways.UserGateway;
import com.project.cuecards.viewModels.AnswerViewModel;
import com.project.cuecards.viewModels.CueCardViewModel;
import com.project.cuecards.viewModels.DataViewModel;
import com.project.cuecards.viewModels.FolderViewModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUsersHomeDataUseCaseTest {

    private final String validUsername = "validUsername";
    private final String invalidUsername = "invalidUsername";
    private GetUsersHomeData useCase;
    @Mock private UserGateway userGatewayMock;
    @Mock private FolderGateway folderGatewayMock;
    private final User validUser = new User();
    private final DataViewModel expectedViewModel = new DataViewModel();
    private final ArrayList<Folder> expectedFolders = new ArrayList<>();
    private final String existingUid = "existingUid";

    @BeforeEach
    void setUp() {
        validUser.setUsername(validUsername);
        useCase = new GetUsersHomeDataUseCase(userGatewayMock, folderGatewayMock);
        expectedViewModel.folders = new ArrayList<>();
    }

    private void prepareMocks() throws UserDoesNotExistException, InvalidArgumentException {
        when(userGatewayMock.getUserByUsername(validUsername)).thenReturn(validUser);
        when(folderGatewayMock.getRootFoldersByUser(validUser)).thenReturn(expectedFolders);
    }

    @Test
    public void testGivenUserDoesNotExist_thenThrowException() throws Exception {
        when(userGatewayMock.getUserByUsername(invalidUsername)).thenThrow(new UserDoesNotExistException());

        Assertions.assertThrows(UserDoesNotExistException.class, () -> useCase.get(invalidUsername));
    }

    @Test
    public void givenRepoThrowsException_thenReturnEmptyViewModel() throws Exception {
        when(userGatewayMock.getUserByUsername(validUsername)).thenReturn(validUser);
        doThrow(InvalidArgumentException.class).when(folderGatewayMock).getRootFoldersByUser(any());

        DataViewModel viewModel = useCase.get(validUsername);

        assertThat(viewModel).usingRecursiveComparison().isEqualTo(new DataViewModel());
    }

    @Test
    public void testGivenUserHasNoFolders_thenReturnEmptyDataViewModel() throws Exception {
        prepareMocks();

        DataViewModel dataViewModel = useCase.get(validUsername);

        assertThat(dataViewModel).usingRecursiveComparison().isEqualTo(new DataViewModel());
    }

    @Test
    public void testGivenUserHasOneFolder_thenReturnDataViewModelWithOneFolder() throws Exception {
        Folder folder = new Folder()
                .setName("Test Folder");
        folder.setUid(existingUid);
        folder.setCreatedBy(validUser);
        expectedFolders.add(folder);
        prepareMocks();
        FolderViewModel expectedFolderViewModel = new FolderViewModel();
        expectedFolderViewModel.ID = existingUid;
        expectedFolderViewModel.name = "Test Folder";
        expectedViewModel.folders.add(expectedFolderViewModel);

        DataViewModel dataViewModel = useCase.get(validUsername);

        assertThat(dataViewModel).usingRecursiveComparison().isEqualTo(expectedViewModel);
    }

    @Test
    public void testGivenUserHasOneSet_thenReturnDataViewModelWithOneSet() throws Exception {
        Folder set = new Folder()
                .setSet(true)
                .setName("Test Set");
        set.setUid(existingUid);
        set.setCreatedBy(validUser);
        expectedFolders.add(set);
        prepareMocks();

        FolderViewModel expectedSetViewModel = new FolderViewModel();
        expectedSetViewModel.isFolder = false;
        expectedSetViewModel.ID = existingUid;
        expectedSetViewModel.name = "Test Set";
        expectedViewModel.folders.add(expectedSetViewModel);

        DataViewModel viewModel = useCase.get(validUsername);

        assertThat(viewModel).usingRecursiveComparison().isEqualTo(expectedViewModel);
    }

    @Test
    public void testGivenUserHasFolderInFolder() throws Exception {
        Folder folder = new Folder()
                .setName("FolderInRoot");
        folder.setUid(existingUid);
        folder.setCreatedBy(validUser);
        Folder subFolder = new Folder();
        subFolder.setUid("different" + existingUid);
        subFolder.setName("SubFolder");
        subFolder.setRootFolder(folder);
        subFolder.setCreatedBy(validUser);
        folder.getSubFolders().add(subFolder);
        expectedFolders.add(folder);
        prepareMocks();

        FolderViewModel expectedFolderViewModel = new FolderViewModel();
        expectedFolderViewModel.ID = existingUid;
        expectedFolderViewModel.name = "FolderInRoot";
        expectedFolderViewModel.subFolders = new ArrayList<>();
        FolderViewModel expectedSubFolderViewModel = new FolderViewModel();
        expectedSubFolderViewModel.ID = "different" + existingUid;
        expectedSubFolderViewModel.name = "SubFolder";
        expectedFolderViewModel.subFolders.add(expectedSubFolderViewModel);
        expectedViewModel.folders.add(expectedFolderViewModel);

        DataViewModel viewModel = useCase.get(validUsername);

        assertThat(viewModel).usingRecursiveComparison().isEqualTo(expectedViewModel);
    }

    @Test
    public void testGivenUserHasSetInFolder() throws Exception {
        Folder folder = new Folder();
        folder.setUid(existingUid);
        folder.setName("FolderInRoot");
        folder.setCreatedBy(validUser);
        Folder set = new Folder();
        set.setSet(true);
        set.setUid("different" + existingUid);
        set.setName("SetInFolder");
        set.setRootFolder(folder);
        set.setCreatedBy(validUser);
        folder.getSubFolders().add(set);
        expectedFolders.add(folder);
        prepareMocks();

        FolderViewModel expectedFolderViewModel = new FolderViewModel();
        expectedFolderViewModel.ID = existingUid;
        expectedFolderViewModel.name = "FolderInRoot";
        expectedFolderViewModel.subFolders = new ArrayList<>();
        FolderViewModel expectedSetViewModel = new FolderViewModel();
        expectedSetViewModel.isFolder = false;
        expectedSetViewModel.ID = "different" + existingUid;
        expectedSetViewModel.name = "SetInFolder";
        expectedFolderViewModel.subFolders.add(expectedSetViewModel);
        expectedViewModel.folders.add(expectedFolderViewModel);

        DataViewModel viewModel = useCase.get(validUsername);

        assertThat(viewModel).usingRecursiveComparison().isEqualTo(expectedViewModel);
    }

    @Test
    public void testGivenUserHasCardInSet() throws Exception {
        Folder set = new Folder();
        set.setUid(existingUid);
        set.setSet(true);
        set.setName("Set");
        set.setCreatedBy(validUser);
        CueCard card = new CueCard();
        card.setUid("different" + existingUid);
        card.setTopic("some Topic");
        card.setQuestion("Frage");
        card.setSolution("Antwort");
        card.setLevel(0);
        card.setSet(set);
        card.setCreatedBy(validUser);
        set.getCueCards().add(card);
        expectedFolders.add(set);
        prepareMocks();

        CueCardViewModel expectedCueCardViewModel = new CueCardViewModel();
        expectedCueCardViewModel.cardID = "different" + existingUid;
        expectedCueCardViewModel.questionText = "Frage";
        expectedCueCardViewModel.solution = "Antwort";
        expectedCueCardViewModel.cardTopic = "some Topic";
        FolderViewModel expectedSetViewModel = new FolderViewModel();
        expectedSetViewModel.isFolder = false;
        expectedSetViewModel.ID = existingUid;
        expectedSetViewModel.name = "Set";
        expectedSetViewModel.cards = new ArrayList<>();
        expectedSetViewModel.cards.add(expectedCueCardViewModel);
        expectedViewModel.folders.add(expectedSetViewModel);

        DataViewModel viewModel = useCase.get(validUsername);

        assertThat(viewModel).usingRecursiveComparison().isEqualTo(expectedViewModel);
    }

    @Test
    public void testGivenFullStructure() throws Exception {
        Folder folder1 = getNewFolder(existingUid + "1");
        Folder folder2 = getNewFolder(existingUid + "2");
        Folder folder3 = getNewFolder(existingUid + "3");
        Folder set4 = getNewSet(existingUid + "4");
        Folder set5 = getNewSet(existingUid + "5");
        Folder set6 = getNewSet(existingUid + "6");
        Folder set7 = getNewSet(existingUid + "7");
        Folder set8 = getNewSet(existingUid + "8");
        CueCard card1 = getNewCueCard(existingUid + "1");
        CueCard card2 = getNewCueCard(existingUid + "2");
        CueCard card3 = getNewCueCard(existingUid + "3");
        CueCard card4 = getNewCueCard(existingUid + "4");
        CueCard card5 = getNewCueCard(existingUid + "5");
        CueCard card6 = getNewCueCard(existingUid + "6");
        CueCard card7 = getNewCueCard(existingUid + "7");
        CueCard card8 = getNewCueCard(existingUid + "8");
        CueCard card9 = getNewCueCard(existingUid + "9");
        CueCard card10 = getNewCueCard(existingUid + "10");

        folder1.getSubFolders().add(folder3);
        folder1.getSubFolders().add(set6);
        folder2.getSubFolders().add(set7);
        folder3.getSubFolders().add(set8);
        set4.getCueCards().add(card1);
        set4.getCueCards().add(card2);
        set5.getCueCards().add(card3);
        set5.getCueCards().add(card4);
        set6.getCueCards().add(card5);
        set6.getCueCards().add(card6);
        set7.getCueCards().add(card7);
        set7.getCueCards().add(card8);
        set8.getCueCards().add(card9);
        set8.getCueCards().add(card10);
        expectedFolders.add(folder1);
        expectedFolders.add(folder2);
        expectedFolders.add(set4);
        expectedFolders.add(set5);
        prepareMocks();

        FolderViewModel folderViewModel1 = getNewFolderViewModel(existingUid + 1);
        FolderViewModel folderViewModel2 = getNewFolderViewModel(existingUid + 2);
        FolderViewModel folderViewModel3 = getNewFolderViewModel(existingUid + 3);
        FolderViewModel setViewModel4 = getNewSetViewModel(existingUid + 4);
        FolderViewModel setViewModel5 = getNewSetViewModel(existingUid + 5);
        FolderViewModel setViewModel6 = getNewSetViewModel(existingUid + 6);
        FolderViewModel setViewModel7 = getNewSetViewModel(existingUid + 7);
        FolderViewModel setViewModel8 = getNewSetViewModel(existingUid + 8);
        CueCardViewModel cardViewModel1 = getNewCardViewModel(existingUid + 1);
        CueCardViewModel cardViewModel2 = getNewCardViewModel(existingUid + 2);
        CueCardViewModel cardViewModel3 = getNewCardViewModel(existingUid + 3);
        CueCardViewModel cardViewModel4 = getNewCardViewModel(existingUid + 4);
        CueCardViewModel cardViewModel5 = getNewCardViewModel(existingUid + 5);
        CueCardViewModel cardViewModel6 = getNewCardViewModel(existingUid + 6);
        CueCardViewModel cardViewModel7 = getNewCardViewModel(existingUid + 7);
        CueCardViewModel cardViewModel8 = getNewCardViewModel(existingUid + 8);
        CueCardViewModel cardViewModel9 = getNewCardViewModel(existingUid + 9);
        CueCardViewModel cardViewModel10 = getNewCardViewModel(existingUid + 10);
        folderViewModel1.subFolders.add(folderViewModel3);
        folderViewModel1.subFolders.add(setViewModel6);
        folderViewModel2.subFolders.add(setViewModel7);
        folderViewModel3.subFolders.add(setViewModel8);
        setViewModel4.cards.add(cardViewModel1);
        setViewModel4.cards.add(cardViewModel2);
        setViewModel5.cards.add(cardViewModel3);
        setViewModel5.cards.add(cardViewModel4);
        setViewModel6.cards.add(cardViewModel5);
        setViewModel6.cards.add(cardViewModel6);
        setViewModel7.cards.add(cardViewModel7);
        setViewModel7.cards.add(cardViewModel8);
        setViewModel8.cards.add(cardViewModel9);
        setViewModel8.cards.add(cardViewModel10);
        expectedViewModel.folders.add(folderViewModel1);
        expectedViewModel.folders.add(folderViewModel2);
        expectedViewModel.folders.add(setViewModel4);
        expectedViewModel.folders.add(setViewModel5);

        DataViewModel viewModel = useCase.get(validUsername);

        assertThat(viewModel).usingRecursiveComparison().isEqualTo(expectedViewModel);
    }

    private CueCardViewModel getNewCardViewModel(String i) {
        CueCardViewModel cardViewModel = new CueCardViewModel();
        cardViewModel.cardID = i;
        cardViewModel.cardTopic = "Topic";
        cardViewModel.questionText = "Frage" + i;
        cardViewModel.solution = "Antwort" + i;
        cardViewModel.cardType = CardType.SC;
        return cardViewModel;
    }

    private FolderViewModel getNewSetViewModel(String i) {
        FolderViewModel setViewModel = new FolderViewModel();
        setViewModel.isFolder = false;
        setViewModel.ID = i;
        setViewModel.name = "Set" + i;
        return setViewModel;
    }

    private FolderViewModel getNewFolderViewModel(String i) {
        FolderViewModel folderViewModel1 = new FolderViewModel();
        folderViewModel1.ID = i;
        folderViewModel1.name = "Folder" + i;
        return folderViewModel1;
    }

    private CueCard getNewCueCard(String i) {
        CueCard card = new CueCard();
        card.setUid(i);
        card.setCreatedBy(validUser);
        card.setTopic("Topic");
        card.setQuestion("Frage" + i);
        card.setSolution("Antwort" + i);
        card.setCardType(CardType.SC);
        card.setLevel(1);
        return card;
    }

    private Folder getNewSet(String i) {
        Folder set = new Folder();
        set.setSet(true);
        set.setUid(i);
        set.setName("Set" + i);
        set.setCreatedBy(validUser);
        return set;
    }

    private Folder getNewFolder(String i) {
        Folder folder = new Folder();
        folder.setUid(i);
        folder.setName("Folder" + i);
        folder.setCreatedBy(validUser);
        return folder;
    }

    @Test
    public void testGivenSetAndFolder_thenReturnFolderAndSet() throws Exception {
        prepareMocks();
        Folder set = getNewSet(existingUid + 1);
        Folder folder = getNewFolder(existingUid + 2);
        CueCard card = getNewCueCard(existingUid + 1);
        set.getCueCards().add(card);
        expectedFolders.add(set);
        expectedFolders.add(folder);
        FolderViewModel folderViewModel = getNewFolderViewModel(existingUid + 2);
        FolderViewModel setViewModel = getNewSetViewModel(existingUid + 1);
        CueCardViewModel cardViewModel = getNewCardViewModel(existingUid + 1);
        setViewModel.cards.add(cardViewModel);
        expectedViewModel.folders.add(folderViewModel);
        expectedViewModel.folders.add(setViewModel);

        DataViewModel viewModel = useCase.get(validUsername);

        assertThat(viewModel).usingRecursiveComparison().isEqualTo(expectedViewModel);
    }

    @Test
    public void givenSetWithCardAndAnswers() throws Exception {
        Folder set = getNewSet(existingUid + 1);
        CueCard card = getNewCueCard(existingUid + 2);
        Answer answer1 = (Answer) new Answer()
                .setText("Answer 1")
                .setCueCard(card)
                .setUid(existingUid + 3);
        Answer answer2 = (Answer) new Answer()
                .setText("Answer 2")
                .setCueCard(card)
                .setUid(existingUid + 4);
        card.getAnswers().add(answer1);
        card.getAnswers().add(answer2);
        set.getCueCards().add(card);
        expectedFolders.add(set);
        prepareMocks();
        FolderViewModel expectedSet = getNewSetViewModel(existingUid + 1);
        CueCardViewModel expectedCard = getNewCardViewModel(existingUid + 2);
        AnswerViewModel expectedAnswer1 = new AnswerViewModel();
        expectedAnswer1.ID = existingUid + 3;
        expectedAnswer1.text = "Answer 1";
        AnswerViewModel expectedAnswer2 = new AnswerViewModel();
        expectedAnswer2.ID = existingUid + 4;
        expectedAnswer2.text = "Answer 2";
        expectedCard.answers.add(expectedAnswer1);
        expectedCard.answers.add(expectedAnswer2);
        expectedSet.cards.add(expectedCard);
        expectedViewModel.folders.add(expectedSet);

        DataViewModel viewModel = useCase.get(validUsername);

        assertThat(viewModel).usingRecursiveComparison().isEqualTo(expectedViewModel);
    }
}