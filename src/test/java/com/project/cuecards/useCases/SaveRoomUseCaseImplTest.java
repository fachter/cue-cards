package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.SaveRoomUseCase;
import com.project.cuecards.entities.Folder;
import com.project.cuecards.entities.Room;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.exceptions.RoomNotFoundException;
import com.project.cuecards.gateways.*;
import com.project.cuecards.services.SaveDataViewModelServiceImpl;
import com.project.cuecards.viewModels.DataViewModel;
import com.project.cuecards.viewModels.FolderViewModel;
import com.project.cuecards.viewModels.RoomViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveRoomUseCaseImplTest {

    @Mock private RoomGateway roomGatewayMock;
    @Mock private FolderGateway folderGatewayMock;
    @Mock private CueCardGateway cueCardGatewayMock;
    @Mock private AnswerGateway answerGatewayMock;
    private SaveRoomUseCase roomUseCase;
    private final User loggedInUser = new User().setNickName("Test").setUsername("test").setPassword("test");
    @Captor private ArgumentCaptor<Room> roomCaptor;
    @Captor private ArgumentCaptor<List<Folder>> folderCaptor;

    @BeforeEach
    void setUp() {
        roomUseCase = new SaveRoomUseCaseImpl(roomGatewayMock,
                new SaveDataViewModelServiceImpl(folderGatewayMock, cueCardGatewayMock, answerGatewayMock));
    }

    @Test
    public void givenRoomViewModelIsNull_thenThrowException() {
        assertThrows(InvalidArgumentException.class, () -> roomUseCase.save(null, new User()));
    }

    @Test
    public void givenUserIsNull_thenThrowException() {
        assertThrows(InvalidArgumentException.class, () -> roomUseCase.save(new RoomViewModel(), null));
    }

    @Test
    public void givenRoomWithoutId_thenSaveNewRoom() throws Exception {
        RoomViewModel viewModel = new RoomViewModel();
        viewModel.name = "Room";
        viewModel.password = "password";
        viewModel.pictureNumber = 2;
        Room expectedRoom = new Room().setName("Room").setPassword("password").setPictureNumber(2);
        expectedRoom.getAllowedUsers().add(loggedInUser);
        loggedInUser.getAvailableRooms().add(expectedRoom);

        roomUseCase.save(viewModel, loggedInUser);

        verify(roomGatewayMock, times(1)).save(roomCaptor.capture());
        assertThat(roomCaptor.getValue()).usingRecursiveComparison().isEqualTo(expectedRoom);
    }

    @Test
    public void givenRoomWithId_thenChangeRoom() throws Exception {
        RoomViewModel viewModel = new RoomViewModel();
        viewModel.id = 123L;
        viewModel.name = "new Name";
        viewModel.password = "newPassword";
        viewModel.pictureNumber = 3;
        Room existingRoom = (Room) new Room().setName("old Name").setPassword("oldPassword").setPictureNumber(2).setId(123L);
        when(roomGatewayMock.getById(123L)).thenReturn(existingRoom);

        roomUseCase.save(viewModel, loggedInUser);

        verify(roomGatewayMock, times(1)).save(roomCaptor.capture());
        assertEquals(existingRoom, roomCaptor.getValue());
        assertEquals("new Name", existingRoom.getName());
        assertEquals("newPassword", existingRoom.getPassword());
        assertEquals(3, existingRoom.getPictureNumber());
    }

    @Test
    public void givenRoomWithEmptyStringAsPassword_thenSaveNull() throws Exception {
        RoomViewModel viewModel = new RoomViewModel();
        viewModel.name = "Test";
        viewModel.password = "";
        viewModel.data = null;
        Room expectedRoom = (Room) new Room().setName("Test").setPassword(null).setId(null);
        expectedRoom.getAllowedUsers().add(loggedInUser);

        roomUseCase.save(viewModel, loggedInUser);

        verify(roomGatewayMock, times(1)).save(roomCaptor.capture());
        Room actualRoom = roomCaptor.getValue();
        assertThat(actualRoom).usingRecursiveComparison().isEqualTo(expectedRoom);
    }

    @Test
    public void givenRoomWithUserAlreadyAddedToAllowedUsers_thenDoNotAddAgain() throws Exception {
        RoomViewModel viewModel = new RoomViewModel();
        viewModel.id = 123L;
        viewModel.name = "name";
        viewModel.password = "password";
        Room existingRoom = (Room) new Room().setName("name").setPassword("password").setId(123L);
        existingRoom.getAllowedUsers().add(loggedInUser);
        when(roomGatewayMock.getById(123L)).thenReturn(existingRoom);

        roomUseCase.save(viewModel, loggedInUser);

        assertEquals(1, existingRoom.getAllowedUsers().size());
    }

    @Test
    public void givenDataViewModel_thenSaveWithRoom() throws Exception {
        RoomViewModel viewModel = new RoomViewModel();
        viewModel.id = 123L;
        viewModel.name = "name";
        viewModel.password = "password";
        viewModel.data = new DataViewModel();
        LocalDateTime lastModified = LocalDateTime.of(2020,1,2,3,4,5,6);
        viewModel.data.lastModified = lastModified;
        FolderViewModel folderViewModel = new FolderViewModel();
        folderViewModel.id = "testUidFolder";
        folderViewModel.name = "Folder";
        viewModel.data.folders.add(folderViewModel);
        when(roomGatewayMock.getById(123L)).thenThrow(RoomNotFoundException.class);

        roomUseCase.save(viewModel, loggedInUser);

        verify(folderGatewayMock, times(1)).saveList(folderCaptor.capture());
        List<Folder> actualFolders = folderCaptor.getValue();
        assertEquals("Folder", actualFolders.get(0).getName());
        assertEquals("testUidFolder", actualFolders.get(0).getUid());
        verify(roomGatewayMock, times(1)).save(roomCaptor.capture());
        Room room = roomCaptor.getValue();
        assertEquals(room, actualFolders.get(0).getRoom());
        assertEquals(lastModified, room.getLastModifiedDateTime());
    }

    @Test
    public void givenSubfolderDoesNotExistAnymore_thenDeleteIt() throws Exception {
        Room room = (Room) new Room().setName("Raum").setPictureNumber(3).setId(123L);
        Folder folder = (Folder) new Folder()
                .setName("RootFolder").setRoom(room).setId(1L).setUid("rootUid");
        Folder subFolder = (Folder) new Folder()
                .setName("SubFolder").setRootFolder(folder).setId(2L).setUid("subUid");
        folder.getSubFolders().add(subFolder);
        room.getFolders().add(folder);
        when(roomGatewayMock.getById(123L)).thenReturn(room);
        RoomViewModel viewModel = new RoomViewModel();
        viewModel.id = 123L;
        viewModel.name = "Raum";
        viewModel.data = new DataViewModel();
        viewModel.data.lastModified = LocalDateTime.of(2020,1,2,3,4,5,6);
        FolderViewModel folderViewModel = new FolderViewModel();
        folderViewModel.id = "rootUid";
        folderViewModel.name = "RootFolder";
        viewModel.data.folders.add(folderViewModel);

        roomUseCase.save(viewModel, loggedInUser);

        verify(folderGatewayMock, times(1)).saveList(folderCaptor.capture());
        List<Folder> foldersToPersist = folderCaptor.getValue();
        assertEquals(1, foldersToPersist.size());
        assertEquals(folder, foldersToPersist.get(0));
        assertEquals(0, folder.getSubFolders().size());
    }
}