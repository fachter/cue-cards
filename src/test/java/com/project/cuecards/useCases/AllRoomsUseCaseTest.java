package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.AllRooms;
import com.project.cuecards.entities.*;
import com.project.cuecards.gateways.RoomGateway;
import com.project.cuecards.services.PrepareDataViewModelServiceImpl;
import com.project.cuecards.viewModels.CueCardViewModel;
import com.project.cuecards.viewModels.DataViewModel;
import com.project.cuecards.viewModels.FolderViewModel;
import com.project.cuecards.viewModels.RoomViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AllRoomsUseCaseTest {

    private AllRooms allRooms;
    @Mock private RoomGateway roomGatewayMock;
    private final User loggedInUser = (User) new User()
            .setUsername("username").setPassword("password").setId(99L);

    @BeforeEach
    void setUp() {
        allRooms = new AllRoomsUseCase(roomGatewayMock, new PrepareDataViewModelServiceImpl());
    }

    @Test
    public void givenNoRoomsAvailable_thenReturnEmptyArrayList() {
        when(roomGatewayMock.getAllAvailableForUser(loggedInUser)).thenReturn(new ArrayList<>());

        ArrayList<RoomViewModel> roomViewModels = allRooms.get(loggedInUser);

        assertEquals(0, roomViewModels.size());
    }

    @Test
    public void givenOneRoom_thenReturnListWithOneRoomViewModel() {
        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add((Room) new Room().setName("Test Room").setPassword("testPassword").setPictureNumber(3).setId(123L));
        when(roomGatewayMock.getAllAvailableForUser(loggedInUser)).thenReturn(rooms);

        ArrayList<RoomViewModel> roomViewModels = allRooms.get(loggedInUser);

        assertEquals(1, roomViewModels.size());
        assertEquals("Test Room", roomViewModels.get(0).name);
        assertEquals(null, roomViewModels.get(0).password);
        assertEquals(3, roomViewModels.get(0).pictureNumber);
        assertEquals(123L, roomViewModels.get(0).id);
    }

    @Test
    public void givenRoomContainsFoldersAndCards_thenAddThemToRoomViewModel() throws Exception {
        ArrayList<Room> rooms = new ArrayList<>();
        Room room = new Room().setName("Test Room").setPassword("testPassword").setPictureNumber(3);
        LocalDateTime lastModified = LocalDateTime.of(2020,1,2,3,4,5,6);
        room.setLastModifiedDateTime(lastModified);
        Folder folder = (Folder) new Folder().setName("Folder").setId(12L).setUid("testUidFolder");
        Folder set = (Folder) new Folder().setSet(true).setName("Set").setId(13L).setUid("testUidSet");
        set.setRootFolder(folder);
        CueCard cueCard = (CueCard) new CueCard().setQuestion("Frage").setSet(set).setId(52L).setUid("testUidCard");
        CardLevel cardLevelFromOtherUser = (CardLevel) new CardLevel()
                .setUser((User) new User().setFullName("Test").setId(88L))
                .setCueCard(cueCard)
                .setUsersCardLevel(5)
                .setId(57L);
        CardLevel cardLevelLoggedInUser = (CardLevel) new CardLevel()
                .setUser(loggedInUser)
                .setCueCard(cueCard)
                .setUsersCardLevel(3)
                .setId(56L);
        cueCard.getCardLevels().add(cardLevelFromOtherUser);
        cueCard.getCardLevels().add(cardLevelLoggedInUser);
        folder.getSubFolders().add(set);
        set.getCueCards().add(cueCard);
        room.getFolders().add(set);
        room.getFolders().add(folder);
        room.setId(123L);
        rooms.add(room);
        when(roomGatewayMock.getAllAvailableForUser(loggedInUser)).thenReturn(rooms);
        RoomViewModel expectedRoomViewModel = new RoomViewModel();
        expectedRoomViewModel.id = 123L;
        expectedRoomViewModel.pictureNumber = 3;
        expectedRoomViewModel.name = "Test Room";
        DataViewModel expectedDataViewModel = new DataViewModel();
        expectedDataViewModel.lastModified = lastModified;
        FolderViewModel folderViewModel = new FolderViewModel();
        folderViewModel.id = "testUidFolder";
        folderViewModel.name = "Folder";
        folderViewModel.isFolder = true;
        FolderViewModel setViewModel = new FolderViewModel();
        setViewModel.isFolder = false;
        setViewModel.name = "Set";
        setViewModel.id = "testUidSet";
        CueCardViewModel cardViewModel = new CueCardViewModel();
        cardViewModel.id = "testUidCard";
        cardViewModel.questionText = "Frage";
        cardViewModel.cardLevel = 3;
        setViewModel.cards.add(cardViewModel);
        folderViewModel.subFolders.add(setViewModel);
        expectedDataViewModel.folders.add(folderViewModel);
        expectedRoomViewModel.data = expectedDataViewModel;

        ArrayList<RoomViewModel> roomViewModels = allRooms.get(loggedInUser);

        assertThat(roomViewModels.get(0)).usingRecursiveComparison().isEqualTo(expectedRoomViewModel);
    }
}