package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.GetRoomsWebUseCase;
import com.project.cuecards.entities.CueCard;
import com.project.cuecards.entities.Folder;
import com.project.cuecards.entities.Room;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.gateways.FolderGateway;
import com.project.cuecards.gateways.RoomGateway;
import com.project.cuecards.services.GetUserRoomsServiceImpl;
import com.project.cuecards.services.PrepareDataViewModelServiceImpl;
import com.project.cuecards.services.UsersHomeDataServiceImpl;
import com.project.cuecards.services.UsersProfileDataServiceImpl;
import com.project.cuecards.viewModels.CueCardViewModel;
import com.project.cuecards.viewModels.DataViewModel;
import com.project.cuecards.viewModels.FolderViewModel;
import com.project.cuecards.viewModels.RoomViewModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetRoomsWebUseCaseImplTest {

    private GetRoomsWebUseCase useCase;
    @Mock private RoomGateway roomGatewayMock;
    @Mock private FolderGateway folderGatewayMock;

    @BeforeEach
    void setUp() {
        useCase = new GetRoomsWebUseCaseImpl(
                new UsersHomeDataServiceImpl(folderGatewayMock, new PrepareDataViewModelServiceImpl()),
                new GetUserRoomsServiceImpl(roomGatewayMock, new PrepareDataViewModelServiceImpl(), new UsersProfileDataServiceImpl()));
    }

    @Test
    public void givenUserIsNull_thenThrowException() {
        Assertions.assertThrows(InvalidDataException.class, () -> useCase.get(null));
    }

    @Test
    public void givenValidUser_thenReturnUsersListOfRooms() throws Exception {
        User loggedInUser = (User) new User().setUsername("Test").setId(123L);
        List<Room> rooms = new ArrayList<>();
        Room room = new Room().setName("Room").setPictureNumber(3);
        rooms.add(room);
        when(roomGatewayMock.getAllAvailableForUser(loggedInUser)).thenReturn(rooms);

        List<RoomViewModel> roomViewModels = useCase.get(loggedInUser);

        assertEquals(2, roomViewModels.size());
        assertEquals("Mein Raum", roomViewModels.get(0).name);
    }

    @Test
    public void givenValidUserWithoutRooms_thenReturnOnlyPrivateRoome() throws Exception {
        User loggedInUser = (User) new User().setUsername("Test").setId(123L);
        List<Room> rooms = new ArrayList<>();
        when(roomGatewayMock.getAllAvailableForUser(loggedInUser)).thenReturn(rooms);

        List<RoomViewModel> roomViewModels = useCase.get(loggedInUser);

        assertEquals(1, roomViewModels.size());
        assertEquals("Mein Raum", roomViewModels.get(0).name);
    }

    @Test
    public void givenValidUserWithHomeDataAndFolderData() throws Exception {
        LocalDateTime now = LocalDateTime.of(2020,1,2,3,4,5,6);
        User loggedInUser = (User) new User().setUsername("Test").setId(123L).setLastModifiedDateTime(now);
        List<Room> rooms = new ArrayList<>();
        Room room = (Room) new Room().setName("Room").setId(3L);
        Folder folder = (Folder) new Folder().setName("Folder").setUid("folderUid");
        Folder set = (Folder) new Folder().setName("Set").setSet(true).setUid("setUid");
        CueCard cueCard = (CueCard) new CueCard().setQuestion("Frage").setUid("cardUid");
        set.getCueCards().add(cueCard);
        folder.getSubFolders().add(set);
        room.getFolders().add(folder);
        rooms.add(room);
        when(roomGatewayMock.getAllAvailableForUser(loggedInUser)).thenReturn(rooms);
        List<Folder> privateRooms = new ArrayList<>();
        Folder privateFolder = (Folder) new Folder().setName("private Folder").setUid("privateFolderUid");
        privateRooms.add(privateFolder);
        when(folderGatewayMock.getRootFoldersByUser(loggedInUser)).thenReturn(privateRooms);
        DataViewModel expectedPrivateData = new DataViewModel();
        expectedPrivateData.lastModified = now;
        FolderViewModel folderViewModel = new FolderViewModel();
        folderViewModel.name = "private Folder";
        folderViewModel.id = "privateFolderUid";
        expectedPrivateData.folders.add(folderViewModel);
        RoomViewModel expectedRoomViewModel = new RoomViewModel();
        expectedRoomViewModel.name = "Room";
        expectedRoomViewModel.id = 3L;
        FolderViewModel publicFolderViewModel = new FolderViewModel();
        publicFolderViewModel.name = "Folder";
        publicFolderViewModel.id = "folderUid";
        FolderViewModel setViewModel = new FolderViewModel();
        setViewModel.name = "Set";
        setViewModel.id = "setUid";
        setViewModel.isFolder = false;
        CueCardViewModel cardViewModel = new CueCardViewModel();
        cardViewModel.questionText = "Frage";
        cardViewModel.id = "cardUid";
        setViewModel.cards.add(cardViewModel);
        publicFolderViewModel.subFolders.add(setViewModel);
        expectedRoomViewModel.data.folders.add(publicFolderViewModel);

        List<RoomViewModel> roomViewModels = useCase.get(loggedInUser);

        assertEquals(2,roomViewModels.size());
        assertThat(roomViewModels.get(0).data).usingRecursiveComparison()
                .isEqualTo(expectedPrivateData);
        assertThat(roomViewModels.get(1)).usingRecursiveComparison()
                .isEqualTo(expectedRoomViewModel);
    }
}