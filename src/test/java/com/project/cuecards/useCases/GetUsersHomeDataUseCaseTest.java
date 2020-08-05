package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.GetUsersHomeData;
import com.project.cuecards.entities.CueCard;
import com.project.cuecards.entities.Folder;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.exceptions.UserDoesNotExistException;
import com.project.cuecards.gateways.FolderGateway;
import com.project.cuecards.gateways.UserGateway;
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
        Folder folder = new Folder();
        folder.setId((long) 1);
        folder.setName("Test Folder");
        folder.setCreatedBy(validUser);
        expectedFolders.add(folder);
        prepareMocks();
        FolderViewModel expectedFolderViewModel = new FolderViewModel();
        expectedFolderViewModel.ID = (long) 1;
        expectedFolderViewModel.name = "Test Folder";
        expectedViewModel.folders.add(expectedFolderViewModel);

        DataViewModel dataViewModel = useCase.get(validUsername);

        assertThat(dataViewModel).usingRecursiveComparison().isEqualTo(expectedViewModel);
    }

    @Test
    public void testGivenUserHasOneSet_thenReturnDataViewModelWithOneSet() throws Exception {
        Folder set = new Folder();
        set.setId(1L);
        set.setSet(true);
        set.setName("Test Set");
        set.setCreatedBy(validUser);
        expectedFolders.add(set);
        prepareMocks();

        FolderViewModel expectedSetViewModel = new FolderViewModel();
        expectedSetViewModel.isFolder = false;
        expectedSetViewModel.ID = (long) 1;
        expectedSetViewModel.name = "Test Set";
        expectedViewModel.folders.add(expectedSetViewModel);

        DataViewModel viewModel = useCase.get(validUsername);

        assertThat(viewModel).usingRecursiveComparison().isEqualTo(expectedViewModel);
    }

    @Test
    public void testGivenUserHasFolderInFolder() throws Exception {
        Folder folder = new Folder();
        folder.setId(1L);
        folder.setName("FolderInRoot");
        folder.setCreatedBy(validUser);
        Folder subFolder = new Folder();
        subFolder.setId(2L);
        subFolder.setName("SubFolder");
        subFolder.setRootFolder(folder);
        subFolder.setCreatedBy(validUser);
        folder.getSubFolders().add(subFolder);
        expectedFolders.add(folder);
        prepareMocks();

        FolderViewModel expectedFolderViewModel = new FolderViewModel();
        expectedFolderViewModel.ID = 1L;
        expectedFolderViewModel.name = "FolderInRoot";
        expectedFolderViewModel.subFolders = new ArrayList<>();
        FolderViewModel expectedSubFolderViewModel = new FolderViewModel();
        expectedSubFolderViewModel.ID = 2L;
        expectedSubFolderViewModel.name = "SubFolder";
        expectedFolderViewModel.subFolders.add(expectedSubFolderViewModel);
        expectedViewModel.folders.add(expectedFolderViewModel);

        DataViewModel viewModel = useCase.get(validUsername);

        assertThat(viewModel).usingRecursiveComparison().isEqualTo(expectedViewModel);
    }

    @Test
    public void testGivenUserHasSetInFolder() throws Exception {
        Folder folder = new Folder();
        folder.setId(1L);
        folder.setName("FolderInRoot");
        folder.setCreatedBy(validUser);
        Folder set = new Folder();
        set.setSet(true);
        set.setId(2L);
        set.setName("SetInFolder");
        set.setRootFolder(folder);
        set.setCreatedBy(validUser);
        folder.getSubFolders().add(set);
        expectedFolders.add(folder);
        prepareMocks();

        FolderViewModel expectedFolderViewModel = new FolderViewModel();
        expectedFolderViewModel.ID = 1L;
        expectedFolderViewModel.name = "FolderInRoot";
        expectedFolderViewModel.subFolders = new ArrayList<>();
        FolderViewModel expectedSetViewModel = new FolderViewModel();
        expectedSetViewModel.isFolder = false;
        expectedSetViewModel.ID = 2L;
        expectedSetViewModel.name = "SetInFolder";
        expectedFolderViewModel.subFolders.add(expectedSetViewModel);
        expectedViewModel.folders.add(expectedFolderViewModel);

        DataViewModel viewModel = useCase.get(validUsername);

        assertThat(viewModel).usingRecursiveComparison().isEqualTo(expectedViewModel);
    }

    @Test
    public void testGivenUserHasCardInSet() throws Exception {
        Folder set = new Folder();
        set.setId(1L);
        set.setSet(true);
        set.setName("Set");
        set.setCreatedBy(validUser);
        CueCard card = new CueCard();
        card.setId(10L);
        card.setTopic("some Topic");
        card.setQuestion("Frage");
        card.setAnswer("Antwort");
        card.setLevel(0);
        card.setSet(set);
        card.setCreatedBy(validUser);
        set.getCueCards().add(card);
        expectedFolders.add(set);
        prepareMocks();

        CueCardViewModel expectedCueCardViewModel = new CueCardViewModel();
        expectedCueCardViewModel.cardID = 10L;
        expectedCueCardViewModel.questionText = "Frage";
        expectedCueCardViewModel.answer = "Antwort";
        expectedCueCardViewModel.cardTopic = "some Topic";
        FolderViewModel expectedSetViewModel = new FolderViewModel();
        expectedSetViewModel.isFolder = false;
        expectedSetViewModel.ID = 1L;
        expectedSetViewModel.name = "Set";
        expectedSetViewModel.cueCards = new ArrayList<>();
        expectedSetViewModel.cueCards.add(expectedCueCardViewModel);
        expectedViewModel.folders.add(expectedSetViewModel);

        DataViewModel viewModel = useCase.get(validUsername);

        assertThat(viewModel).usingRecursiveComparison().isEqualTo(expectedViewModel);
    }

    @Test
    public void testGivenFullStructure() throws Exception {
        Folder folder1 = getNewFolder(1);
        Folder folder2 = getNewFolder(2);
        Folder folder3 = getNewFolder(3);
        Folder set4 = getNewSet(4);
        Folder set5 = getNewSet(5);
        Folder set6 = getNewSet(6);
        Folder set7 = getNewSet(7);
        Folder set8 = getNewSet(8);
        CueCard card1 = getNewCueCard(1);
        CueCard card2 = getNewCueCard(2);
        CueCard card3 = getNewCueCard(3);
        CueCard card4 = getNewCueCard(4);
        CueCard card5 = getNewCueCard(5);
        CueCard card6 = getNewCueCard(6);
        CueCard card7 = getNewCueCard(7);
        CueCard card8 = getNewCueCard(8);
        CueCard card9 = getNewCueCard(9);
        CueCard card10 = getNewCueCard(10);

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

        FolderViewModel folderViewModel1 = getNewFolderViewModel(1);
        FolderViewModel folderViewModel2 = getNewFolderViewModel(2);
        FolderViewModel folderViewModel3 = getNewFolderViewModel(3);
        FolderViewModel setViewModel4 = getNewSetViewModel(4);
        FolderViewModel setViewModel5 = getNewSetViewModel(5);
        FolderViewModel setViewModel6 = getNewSetViewModel(6);
        FolderViewModel setViewModel7 = getNewSetViewModel(7);
        FolderViewModel setViewModel8 = getNewSetViewModel(8);
        CueCardViewModel cardViewModel1 = getNewCardViewModel(1);
        CueCardViewModel cardViewModel2 = getNewCardViewModel(2);
        CueCardViewModel cardViewModel3 = getNewCardViewModel(3);
        CueCardViewModel cardViewModel4 = getNewCardViewModel(4);
        CueCardViewModel cardViewModel5 = getNewCardViewModel(5);
        CueCardViewModel cardViewModel6 = getNewCardViewModel(6);
        CueCardViewModel cardViewModel7 = getNewCardViewModel(7);
        CueCardViewModel cardViewModel8 = getNewCardViewModel(8);
        CueCardViewModel cardViewModel9 = getNewCardViewModel(9);
        CueCardViewModel cardViewModel10 = getNewCardViewModel(10);
        folderViewModel1.subFolders.add(folderViewModel3);
        folderViewModel1.subFolders.add(setViewModel6);
        folderViewModel2.subFolders.add(setViewModel7);
        folderViewModel3.subFolders.add(setViewModel8);
        setViewModel4.cueCards.add(cardViewModel1);
        setViewModel4.cueCards.add(cardViewModel2);
        setViewModel5.cueCards.add(cardViewModel3);
        setViewModel5.cueCards.add(cardViewModel4);
        setViewModel6.cueCards.add(cardViewModel5);
        setViewModel6.cueCards.add(cardViewModel6);
        setViewModel7.cueCards.add(cardViewModel7);
        setViewModel7.cueCards.add(cardViewModel8);
        setViewModel8.cueCards.add(cardViewModel9);
        setViewModel8.cueCards.add(cardViewModel10);
        expectedViewModel.folders.add(folderViewModel1);
        expectedViewModel.folders.add(folderViewModel2);
        expectedViewModel.folders.add(setViewModel4);
        expectedViewModel.folders.add(setViewModel5);

        DataViewModel viewModel = useCase.get(validUsername);

        assertThat(viewModel).usingRecursiveComparison().isEqualTo(expectedViewModel);
    }

    private CueCardViewModel getNewCardViewModel(long i) {
        CueCardViewModel cardViewModel = new CueCardViewModel();
        cardViewModel.cardID = i;
        cardViewModel.cardTopic = "Topic";
        cardViewModel.questionText = "Frage" + i;
        cardViewModel.answer = "Antwort" + i;
        return cardViewModel;
    }

    private FolderViewModel getNewSetViewModel(long i) {
        FolderViewModel setViewModel = new FolderViewModel();
        setViewModel.isFolder = false;
        setViewModel.ID = i;
        setViewModel.name = "Set" + i;
        return setViewModel;
    }

    private FolderViewModel getNewFolderViewModel(long i) {
        FolderViewModel folderViewModel1 = new FolderViewModel();
        folderViewModel1.ID = i;
        folderViewModel1.name = "Folder" + i;
        return folderViewModel1;
    }

    private CueCard getNewCueCard(long i) {
        CueCard card = new CueCard();
        card.setId(i);
        card.setCreatedBy(validUser);
        card.setTopic("Topic");
        card.setQuestion("Frage" + i);
        card.setAnswer("Antwort" + i);
        card.setLevel(1);
        return card;
    }

    private Folder getNewSet(long i) {
        Folder set = new Folder();
        set.setSet(true);
        set.setId(i);
        set.setName("Set" + i);
        set.setCreatedBy(validUser);
        return set;
    }

    private Folder getNewFolder(long i) {
        Folder folder = new Folder();
        folder.setId(i);
        folder.setName("Folder" + i);
        folder.setCreatedBy(validUser);
        return folder;
    }

    @Test
    public void testGivenSetAndFolder_thenReturnFolderAndSet() throws Exception {
        Folder set = getNewSet(1);
        Folder folder = getNewFolder(2);
        CueCard card = getNewCueCard(1);
        set.getCueCards().add(card);
        expectedFolders.add(set);
        expectedFolders.add(folder);
        prepareMocks();
        FolderViewModel folderViewModel = getNewFolderViewModel(2);
        FolderViewModel setViewModel = getNewSetViewModel(1);
        CueCardViewModel cardViewModel = getNewCardViewModel(1);
        setViewModel.cueCards.add(cardViewModel);
        expectedViewModel.folders.add(folderViewModel);
        expectedViewModel.folders.add(setViewModel);

        DataViewModel viewModel = useCase.get(validUsername);

        assertThat(viewModel).usingRecursiveComparison().isEqualTo(expectedViewModel);
    }
}