package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.SaveUsersHomeData;
import com.project.cuecards.entities.Answer;
import com.project.cuecards.entities.CueCard;
import com.project.cuecards.entities.Folder;
import com.project.cuecards.entities.User;
import com.project.cuecards.enums.CardType;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.exceptions.InvalidDataException;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaveUsersHomeDataUseCaseTest {

    private final String newSetUid = "SomeNewSetUid";
    private final String newFolderUid = "SomeNewFolderUid";
    private final String newCardUid = "SomeNewCardId";
    private final String existingUid = "SomeExistingUid";
    @Mock private FolderGateway folderGatewayMock;
    @Mock private UserGateway userGatewayMock;
    @Captor private ArgumentCaptor<ArrayList<Folder>> captor;
    private SaveUsersHomeData useCase;
    private DataViewModel viewModel;
    private final String validUsername = "validUsername";
    private final User validUser = new User();

    @BeforeEach
    void setUp() {
        useCase = new SaveUsersHomeDataUseCase(folderGatewayMock, userGatewayMock);
        viewModel = new DataViewModel();
    }

    private FolderViewModel createSetViewModel(String setName) {
        FolderViewModel setViewModel = new FolderViewModel();
        setViewModel.isFolder = false;
        setViewModel.ID = newSetUid;
        setViewModel.name = setName;
        return setViewModel;
    }

    private FolderViewModel createFolderViewModel(String folderName) {
        FolderViewModel folderViewModel = new FolderViewModel();
        folderViewModel.ID = newFolderUid;
        folderViewModel.name = folderName;
        return folderViewModel;
    }

    private CueCardViewModel createCueCardViewModel(String question, String answer) {
        CueCardViewModel card = new CueCardViewModel();
        card.cardID = newCardUid;
        card.cardTopic = "Topic";
        card.questionText = question;
        card.solution = answer;
        return card;
    }

    @Test
    public void givenAddListsThrowsException() throws Exception {
        doThrow(new InvalidArgumentException()).when(folderGatewayMock).addList(any());
        FolderViewModel test = createFolderViewModel("Test");
        viewModel.folders.add(test);

        Assertions.assertThrows(InvalidDataException.class, () -> useCase.save(viewModel, null));
    }

    @Test
    public void givenNoFolderAndSets_thenNoDbCall() throws Exception {
        useCase.save(new DataViewModel(), null);
        verify(folderGatewayMock, never()).addList(any());
    }

    @Test
    public void givenUserGatewayThrowsException_thenThrowException() throws Exception {
        when(userGatewayMock.getUserByUsername("invalidUsername")).thenThrow(UserDoesNotExistException.class);
        FolderViewModel folderViewModel = createFolderViewModel("Test");
        viewModel.folders.add(folderViewModel);
        Folder expectedFolder = new Folder();
        expectedFolder.setCreatedBy(null);
        expectedFolder.setName("Test");

        Assertions.assertThrows(UserDoesNotExistException.class, () ->
                useCase.save(viewModel, "invalidUsername"));
    }

    @Test
    public void givenOneNewFolder() throws Exception {
        when(userGatewayMock.getUserByUsername(validUsername)).thenReturn(validUser);
        when(folderGatewayMock.getRootFoldersByUser(validUser)).thenReturn(new ArrayList<>());
        FolderViewModel folderViewModel = createFolderViewModel("Test");
        viewModel.folders.add(folderViewModel);
        ArrayList<Folder> expectedFolders = new ArrayList<>();
        Folder folder = new Folder();
        folder.setUid(newFolderUid);
        folder.setName("Test");
        folder.setCreatedBy(validUser);
        expectedFolders.add(folder);

        useCase.save(viewModel, validUsername);

        verify(folderGatewayMock, times(1)).addList(captor.capture());
        ArrayList<Folder> actualFolders = captor.getValue();
        assertThat(actualFolders.get(0)).usingRecursiveComparison().isEqualTo(folder);
        assertThat(captor.getValue()).usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(expectedFolders);
    }

    @Test
    public void givenOneFolderOneSet() throws Exception {
        FolderViewModel folderViewModel = createFolderViewModel("Folder");
        FolderViewModel setViewModel = createSetViewModel("Set");
        viewModel.folders.add(folderViewModel);
        viewModel.folders.add(setViewModel);
        ArrayList<Folder> expectedFolders = new ArrayList<>();
        Folder folder = (Folder) new Folder().setName("Folder").setUid(newFolderUid);
        Folder set = (Folder) new Folder().setName("Set").setSet(true).setUid(newSetUid);
        expectedFolders.add(folder);
        expectedFolders.add(set);

        useCase.save(viewModel, null);

        verify(folderGatewayMock, times(1)).addList(captor.capture());
        ArrayList<Folder> actualFolders = captor.getValue();
        assertThat(actualFolders.get(0)).usingRecursiveComparison().isEqualTo(folder);
        assertThat(actualFolders.get(1)).usingRecursiveComparison().isEqualTo(set);
        assertThat(captor.getValue()).usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(expectedFolders);
    }

    @Test
    public void givenOneFolderOneSetOneCard() throws Exception {
        when(userGatewayMock.getUserByUsername(validUsername)).thenReturn(validUser);
        when(folderGatewayMock.getRootFoldersByUser(validUser)).thenReturn(new ArrayList<>());
        FolderViewModel folderVm = createFolderViewModel("testFolder");
        FolderViewModel setVm = createSetViewModel("testSet");
        CueCardViewModel cardVm = createCueCardViewModel("testQuestion", "testAnswer");
        setVm.cards.add(cardVm);
        viewModel.folders.add(folderVm);
        viewModel.folders.add(setVm);
        Folder folder = new Folder()
                .setName("testFolder");
        folder.setCreatedBy(validUser);
        folder.setUid(newFolderUid);
        Folder set = new Folder()
                .setName("testSet")
                .setSet(true)
                .setRootFolder(null);
        set.setCreatedBy(validUser);
        set.setUid(newSetUid);
        CueCard card = new CueCard()
                .setSet(set)
                .setTopic("Topic")
                .setQuestion("testQuestion")
                .setSolution("testAnswer");
        card.setCreatedBy(validUser);
        card.setUid(newCardUid);
        set.getCueCards().add(card);

        useCase.save(viewModel, validUsername);

        verify(folderGatewayMock, times(1)).addList(captor.capture());
        ArrayList<Folder> actualFolders = captor.getValue();
        assertThat(actualFolders.get(0)).usingRecursiveComparison().isEqualTo(folder);
        assertThat(actualFolders.get(1)).usingRecursiveComparison().isEqualTo(set);
    }

    @Test
    public void givenFolderWithSubFolder() throws Exception {
        FolderViewModel folderViewModel = createFolderViewModel("Root");
        FolderViewModel subFolderViewModel = createFolderViewModel("Sub Folder");
        subFolderViewModel.ID = "subFolderUid";
        folderViewModel.subFolders.add(subFolderViewModel);
        viewModel.folders.add(folderViewModel);
        Folder expectedRootFolder = new Folder()
                .setName("Root");
        expectedRootFolder.setUid(newFolderUid);
        Folder expectedSubFolder = new Folder()
                .setName("Sub Folder")
                .setRootFolder(expectedRootFolder);
        expectedSubFolder.setUid("subFolderUid");
        expectedRootFolder.getSubFolders().add(expectedSubFolder);

        useCase.save(viewModel, null);

        verify(folderGatewayMock, times(1)).addList(captor.capture());
        ArrayList<Folder> actualFolders = captor.getValue();
        assertEquals(1, actualFolders.size());
        assertThat(actualFolders.get(0)).usingRecursiveComparison().isEqualTo(expectedRootFolder);
    }

    @Test
    public void givenFolderWithSetInside() throws Exception {
        FolderViewModel folderViewModel = createFolderViewModel("Root");
        FolderViewModel setViewModel = createSetViewModel("sub set");
        folderViewModel.subFolders.add(setViewModel);
        viewModel.folders.add(folderViewModel);
        Folder expectedRootFolder = new Folder()
                .setName("Root");
        expectedRootFolder.setUid(newFolderUid);
        Folder expectedSubSet = new Folder()
                .setName("sub set")
                .setRootFolder(expectedRootFolder)
                .setSet(true);
        expectedSubSet.setUid(newSetUid);
        expectedRootFolder.getSubFolders().add(expectedSubSet);

        useCase.save(viewModel, null);

        verify(folderGatewayMock, times(1)).addList(captor.capture());
        ArrayList<Folder> actualFolders = captor.getValue();
        assertEquals(1, actualFolders.size());
        assertThat(actualFolders.get(0)).usingRecursiveComparison().isEqualTo(expectedRootFolder);
    }

    @Test
    public void givenCardInSet() throws Exception {
        when(userGatewayMock.getUserByUsername(validUsername)).thenReturn(validUser);
        FolderViewModel setViewModel = createSetViewModel("set");
        CueCardViewModel card1 = createCueCardViewModel("Frage 1", "Antwort 1");
        card1.cardID += "1";
        CueCardViewModel card2 = createCueCardViewModel("Frage 2", "Antwort 2");
        card2.cardID += "2";
        setViewModel.cards.add(card1);
        setViewModel.cards.add(card2);
        viewModel.folders.add(setViewModel);
        Folder expectedSet = new Folder()
                .setName("set")
                .setSet(true);
        expectedSet.setCreatedBy(validUser);
        expectedSet.setUid(newSetUid);
        CueCard expectedCard1 = new CueCard()
                .setTopic("Topic")
                .setQuestion("Frage 1")
                .setSolution("Antwort 1")
                .setSet(expectedSet);
        expectedCard1.setCreatedBy(validUser);
        expectedCard1.setUid(newCardUid + 1);
        CueCard expectedCard2 = new CueCard()
                .setTopic("Topic")
                .setQuestion("Frage 2")
                .setSolution("Antwort 2")
                .setSet(expectedSet);
        expectedCard2.setCreatedBy(validUser);
        expectedCard2.setUid(newCardUid+ 2);
        expectedSet.getCueCards().add(expectedCard1);
        expectedSet.getCueCards().add(expectedCard2);

        useCase.save(viewModel, validUsername);

        verify(folderGatewayMock, times(1)).addList(captor.capture());
        ArrayList<Folder> actualFolders = captor.getValue();
        assertThat(actualFolders.get(0)).usingRecursiveComparison().isEqualTo(expectedSet);
    }

    @Test
    public void givenFolderAlreadyExists_thenEdit() throws Exception {
        when(userGatewayMock.getUserByUsername(validUsername)).thenReturn(validUser);
        FolderViewModel folderViewModel = createFolderViewModel("existingFolder");
        folderViewModel.ID = existingUid;
        viewModel.folders.add(folderViewModel);
        Folder folder = new Folder()
                .setName("oldName");
        folder.setUid(existingUid);
        ArrayList<Folder> folders = new ArrayList<>();
        folders.add(folder);
        when(folderGatewayMock.getRootFoldersByUser(validUser)).thenReturn(folders);

        useCase.save(viewModel, validUsername);

        verify(folderGatewayMock, times(1)).addList(captor.capture());

        ArrayList<Folder> actualFolders = captor.getValue();
        assertEquals("existingFolder", folder.getName());
        assertTrue(actualFolders.contains(folder));
    }

    @Test
    public void givenFolderHasIdButDoesNotExist_thenAdd() throws Exception {
        when(userGatewayMock.getUserByUsername(validUsername)).thenReturn(validUser);
        FolderViewModel folderViewModel = createFolderViewModel("test");
        folderViewModel.ID = existingUid;
        viewModel.folders.add(folderViewModel);
        when(folderGatewayMock.getRootFoldersByUser(validUser)).thenReturn(new ArrayList<>());

        useCase.save(viewModel, validUsername);

        verify(folderGatewayMock, times(1)).addList(captor.capture());
        ArrayList<Folder> actualFolders = captor.getValue();
        assertEquals(1, actualFolders.size());
    }

    @Test
    public void givenFolderWith2SubFoldersAnd2CardsEach() throws Exception {
        when(userGatewayMock.getUserByUsername(validUsername)).thenReturn(validUser);
        FolderViewModel f1 = createFolderViewModel("f1");
        f1.ID = "uid1";
        FolderViewModel f2 = createFolderViewModel("f2");
        f2.ID = "uid2";
        FolderViewModel s1 = createSetViewModel("s1");
        s1.ID = "uid3";
        FolderViewModel s2 = createSetViewModel("s2");
        s2.ID = "uid4";
        CueCardViewModel c1 = createCueCardViewModel("question1", "answer1");
        c1.cardLevel = 3;
        c1.cardID = "uid5";
        CueCardViewModel c2 = createCueCardViewModel("question2", "answer2");
        c2.cardLevel = 5;
        c2.cardID = "uid6";
        s1.cards.add(c1);
        s2.cards.add(c2);
        f2.subFolders.add(s2);
        f1.subFolders.add(s1);
        f1.subFolders.add(f2);
        viewModel.folders.add(f1);
        Folder expectedF1 = createFolder("f1", "uid1");
        Folder expectedF2 = createFolder("f2", "uid2");
        Folder expectedS1 = createSet("s1", "uid3");
        Folder expectedS2 = createSet("s2", "uid4");
        CueCard expectedC1 = new CueCard()
                .setTopic("Topic")
                .setQuestion("question1")
                .setSolution("answer1")
                .setLevel(3);
        expectedC1.setUid("uid5");
        expectedC1.setCreatedBy(validUser);
        CueCard expectedC2 = new CueCard()
                .setTopic("Topic")
                .setQuestion("question2")
                .setSolution("answer2")
                .setLevel(5);
        expectedC2.setUid("uid6");
        expectedC2.setCreatedBy(validUser);

        expectedC1.setSet(expectedS1);
        expectedC2.setSet(expectedS2);
        expectedS1.getCueCards().add(expectedC1);
        expectedS2.getCueCards().add(expectedC2);
        expectedS1.setRootFolder(expectedF1);
        expectedS2.setRootFolder(expectedF2);
        expectedF1.getSubFolders().add(expectedS1);
        expectedF1.getSubFolders().add(expectedF2);
        expectedF2.getSubFolders().add(expectedS2);
        expectedF2.setRootFolder(expectedF1);

        useCase.save(viewModel, validUsername);

        verify(folderGatewayMock, times(1)).addList(captor.capture());
        ArrayList<Folder> actualFolders = captor.getValue();
        assertEquals(1, actualFolders.size());
        assertThat(actualFolders.get(0)).usingRecursiveComparison().isEqualTo(expectedF1);
    }

    private Folder createFolder(String name, String uid) {
        Folder folder = new Folder().setName(name);
        folder.setCreatedBy(validUser);
        folder.setUid(uid);
        return folder;
    }

    private Folder createSet(String name, String uid) {
        return createFolder(name, uid).setSet(true);
    }

    @Test
    public void givenOneSetWithOneCardWithMcOrScAnswers() throws Exception {
        when(userGatewayMock.getUserByUsername(validUsername)).thenReturn(validUser);
        when(folderGatewayMock.getRootFoldersByUser(validUser)).thenReturn(new ArrayList<>());
        FolderViewModel setViewModel = createSetViewModel("set");
        setViewModel.ID = "setUid";
        CueCardViewModel cardViewModel = new CueCardViewModel();
        cardViewModel.questionText = "Frage";
        cardViewModel.cardLevel = 2;
        cardViewModel.cardID = "someCoolNewCardId";
        cardViewModel.cardTopic = "cardTopic";
        cardViewModel.cardType = CardType.MC;
        AnswerViewModel answerVm1 = new AnswerViewModel();
        answerVm1.ID = "antwort1Id";
        answerVm1.text = "erste Antwort";
        AnswerViewModel answerVm2 = new AnswerViewModel();
        answerVm2.ID = "antwort2Id";
        answerVm2.text = "zweite Antwort";
        cardViewModel.answers.add(answerVm1);
        cardViewModel.answers.add(answerVm2);
        setViewModel.cards.add(cardViewModel);
        viewModel.folders.add(setViewModel);
        Folder expectedSet = createSet("set", "setUid");
        CueCard card = createCueCard(expectedSet);
        Answer answer1 = new Answer()
                .setText("erste Antwort")
                .setCueCard(card);
        answer1.setUid("antwort1Id");
        Answer answer2 = new Answer()
                .setText("zweite Antwort")
                .setCueCard(card);
        answer2.setUid("antwort2Id");
        card.getAnswers().add(answer1);
        card.getAnswers().add(answer2);
        expectedSet.getCueCards().add(card);

        useCase.save(viewModel, validUsername);

        verify(folderGatewayMock, times(1)).addList(captor.capture());
        ArrayList<Folder> actualFolders = captor.getValue();
        assertEquals(1, actualFolders.size());
        assertThat(actualFolders.get(0)).usingRecursiveComparison().isEqualTo(expectedSet);
    }

    private CueCard createCueCard(Folder set) {
        CueCard card = new CueCard()
                .setSet(set)
                .setTopic("cardTopic")
                .setCardType(CardType.MC)
                .setQuestion("Frage")
                .setLevel(2);
        card.setUid("someCoolNewCardId");
        card.setCreatedBy(validUser);
        return card;
    }

    @Test
    public void givenSaveExistingDataViewModel() throws Exception {
        Folder folder = createFolder("folder", "folderId");
        Folder set = createSet("set", "setId");
        set.setRootFolder(folder);
        CueCard card = createCueCard(set);
        Answer answer = (Answer) new Answer()
                .setText("answer")
                .setCueCard(card)
                .setUid("answerId");
        card.getAnswers().add(answer);
        set.getCueCards().add(card);
        folder.getSubFolders().add(set);
        when(userGatewayMock.getUserByUsername(validUsername)).thenReturn(validUser);
        ArrayList<Folder> folders = new ArrayList<>();
        folders.add(folder);
        when(folderGatewayMock.getRootFoldersByUser(validUser)).thenReturn(folders);

        FolderViewModel folderVm = createFolderViewModel("changed Folder");
        folderVm.ID = "folderId";
        FolderViewModel setVm = createSetViewModel("changed Set");
        setVm.ID = "setId";
        CueCardViewModel cardVm = createCueCardViewModel("changed Frage", null);
        cardVm.cardTopic = "changed Topic";
        cardVm.cardType = CardType.SC;
        cardVm.cardLevel = 3;
        cardVm.cardID = "someCoolNewCardId";
        AnswerViewModel answerVm = new AnswerViewModel();
        answerVm.text = "changed answer";
        answerVm.ID = "answerId";
        cardVm.answers.add(answerVm);
        setVm.cards.add(cardVm);
        folderVm.subFolders.add(setVm);
        viewModel.folders.add(folderVm);

        useCase.save(viewModel, validUsername);

        verify(folderGatewayMock, times(1)).addList(captor.capture());
        ArrayList<Folder> actualFolders = captor.getValue();
        assertEquals(1, actualFolders.size());
        assertThat(actualFolders.get(0)).usingRecursiveComparison().isEqualTo(folder);
        assertEquals("changed Folder" ,folder.getName());
        assertEquals("changed Set" ,set.getName());
        assertEquals("changed Frage", card.getQuestion());
        assertEquals("changed Topic", card.getTopic());
        assertEquals(CardType.SC, card.getCardType());
        assertEquals("changed answer", answer.getText());
    }
}
