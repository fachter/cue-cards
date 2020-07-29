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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.constraints.AssertTrue;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaveUsersHomeDataUseCaseTest {

    @Mock private FolderGateway folderGatewayMock;
    @Mock private UserGateway userGatewayMock;

    @Captor
    private ArgumentCaptor<ArrayList<Folder>> captor;

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
        setViewModel.id = 0L;
        setViewModel.name = setName;
        return setViewModel;
    }

    private FolderViewModel createFolderViewModel(String folderName) {
        FolderViewModel folderViewModel = new FolderViewModel();
        folderViewModel.id = 0L;
        folderViewModel.name = folderName;
        return folderViewModel;
    }

    private CueCardViewModel createCueCardViewModel(String question, String answer) {
        CueCardViewModel card = new CueCardViewModel();
        card.id = 0L;
        card.topic = "Topic";
        card.question = question;
        card.answer = answer;
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
    public void givenOneFolder() throws Exception {
        FolderViewModel folderViewModel = createFolderViewModel("Test");
        viewModel.folders.add(folderViewModel);
        ArrayList<Folder> expectedFolders = new ArrayList<>();
        Folder folder = new Folder();
        folder.setName("Test");
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
        Folder folder = new Folder();
        folder.setName("Folder");
        Folder set = new Folder();
        set.setName("Set");
        set.setSet(true);
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
    public void givenUsernameThenAddUser() throws Exception {
        when(userGatewayMock.getUserByUsername(validUsername)).thenReturn(validUser);
        FolderViewModel folderVm = createFolderViewModel("testFolder");
        FolderViewModel setVm = createSetViewModel("testSet");
        CueCardViewModel cardVm = createCueCardViewModel("testQuestion", "testAnswer");
        setVm.cueCards.add(cardVm);
        folderVm.subFolders.add(setVm);
        viewModel.folders.add(folderVm);
        Folder folder = new Folder();
        folder.setName("testFolder");
        folder.setCreatedBy(validUser);
        Folder set = new Folder();
        set.setName("testSet");
        set.setSet(true);
        set.setCreatedBy(validUser);
        set.setRootFolder(folder);
        CueCard card = new CueCard();
        card.setSet(set);
        card.setTopic("Topic");
        card.setQuestion("testQuestion");
        card.setAnswer("testAnswer");
        card.setCreatedBy(validUser);
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
        folderViewModel.subFolders.add(subFolderViewModel);
        viewModel.folders.add(folderViewModel);
        Folder expectedRootFolder = new Folder();
        expectedRootFolder.setName("Root");
        Folder expectedSubFolder = new Folder();
        expectedSubFolder.setName("Sub Folder");
        expectedSubFolder.setRootFolder(expectedRootFolder);
        ArrayList<Folder> expectedFolders = new ArrayList<>();
        expectedFolders.add(expectedRootFolder);
        expectedFolders.add(expectedSubFolder);

        useCase.save(viewModel, null);

        verify(folderGatewayMock, times(1)).addList(captor.capture());
        assertThat(captor.getValue()).usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(expectedFolders);
        ArrayList<Folder> actualFolders = captor.getValue();
        assertThat(actualFolders.get(0)).usingRecursiveComparison().isEqualTo(expectedRootFolder);
        assertThat(actualFolders.get(1)).usingRecursiveComparison().isEqualTo(expectedSubFolder);
    }

    @Test
    public void givenFolderWithSetInside() throws Exception {
        FolderViewModel folderViewModel = createFolderViewModel("Root");
        FolderViewModel setViewModel = createSetViewModel("sub set");
        folderViewModel.subFolders.add(setViewModel);
        viewModel.folders.add(folderViewModel);
        Folder expectedRootFolder = new Folder();
        expectedRootFolder.setName("Root");
        Folder expectedSubSet = new Folder();
        expectedSubSet.setName("sub set");
        expectedSubSet.setRootFolder(expectedRootFolder);
        expectedSubSet.setSet(true);

        useCase.save(viewModel, null);

        verify(folderGatewayMock, times(1)).addList(captor.capture());
        ArrayList<Folder> actualFolders = captor.getValue();
        assertThat(actualFolders.get(0)).usingRecursiveComparison().isEqualTo(expectedRootFolder);
        assertThat(actualFolders.get(1)).usingRecursiveComparison().isEqualTo(expectedSubSet);
    }

    @Test
    public void givenCardInSet() throws Exception {
        when(userGatewayMock.getUserByUsername(validUsername)).thenReturn(validUser);
        FolderViewModel setViewModel = createSetViewModel("set");
        CueCardViewModel card1 = createCueCardViewModel("Frage 1", "Antwort 1");
        CueCardViewModel card2 = createCueCardViewModel("Frage 2", "Antwort 2");
        setViewModel.cueCards.add(card1);
        setViewModel.cueCards.add(card2);
        viewModel.folders.add(setViewModel);
        Folder expectedSet = new Folder();
        expectedSet.setName("set");
        expectedSet.setSet(true);
        expectedSet.setCreatedBy(validUser);
        CueCard expectedCard1 = new CueCard();
        expectedCard1.setTopic("Topic");
        expectedCard1.setQuestion("Frage 1");
        expectedCard1.setAnswer("Antwort 1");
        expectedCard1.setSet(expectedSet);
        expectedCard1.setCreatedBy(validUser);
        CueCard expectedCard2 = new CueCard();
        expectedCard2.setTopic("Topic");
        expectedCard2.setQuestion("Frage 2");
        expectedCard2.setAnswer("Antwort 2");
        expectedCard2.setSet(expectedSet);
        expectedCard2.setCreatedBy(validUser);
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
        folderViewModel.id = 5L;
        viewModel.folders.add(folderViewModel);
        Folder folder = new Folder();
        folder.setId(5L);
        folder.setName("oldName");
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
        folderViewModel.id = 1L;
        viewModel.folders.add(folderViewModel);
        when(folderGatewayMock.getRootFoldersByUser(validUser)).thenReturn(new ArrayList<>());

        useCase.save(viewModel, validUsername);

        verify(folderGatewayMock, times(1)).addList(captor.capture());
        ArrayList<Folder> actualFolders = captor.getValue();
        assertEquals(1, actualFolders.size());
    }
}
