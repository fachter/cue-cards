package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.GetAllRoomsUseCase;
import com.project.cuecards.entities.*;
import com.project.cuecards.gateways.RoomGateway;
import com.project.cuecards.services.GetUserRoomsServiceImpl;
import com.project.cuecards.services.PrepareDataViewModelServiceImpl;
import com.project.cuecards.services.UsersProfileDataServiceImpl;
import com.project.cuecards.viewModels.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllRoomsUseCaseImplTest {

    private static final String DEFAULT_IMAGE_URL = "https://res.cloudinary.com/dilnshj2a/image/upload/v1600454278/ProfilePictures/xsfgjilvywtwnazbsz8g.jpg";
    private GetAllRoomsUseCase getAllRoomsUseCase;
    @Mock private RoomGateway roomGatewayMock;
    private final User loggedInUser = (User) new User()
            .setUsername("username").setPassword("password").setNickName("Test User").setId(999L);

    @BeforeEach
    void setUp() {
        getAllRoomsUseCase = new GetAllRoomsUseCaseImpl(new GetUserRoomsServiceImpl(roomGatewayMock, new PrepareDataViewModelServiceImpl(), new UsersProfileDataServiceImpl()));
    }

    @Test
    public void givenNoRoomsAvailable_thenReturnEmptyArrayList() {
        when(roomGatewayMock.getAllAvailableForUser(loggedInUser)).thenReturn(new ArrayList<>());

        List<RoomViewModel> roomViewModels = getAllRoomsUseCase.get(loggedInUser);

        assertEquals(0, roomViewModels.size());
    }

    @Test
    public void givenOneRoom_thenReturnListWithOneRoomViewModel() {
        ArrayList<Room> rooms = new ArrayList<>();
        Room room = (Room) new Room().setName("Test Room").setPassword("testPassword").setPictureNumber(3).setId(123L);
        room.getAllowedUsers().add(loggedInUser);
        rooms.add(room);
        when(roomGatewayMock.getAllAvailableForUser(loggedInUser)).thenReturn(rooms);
        loggedInUser.setPictureUrl("test.url");

        List<RoomViewModel> roomViewModels = getAllRoomsUseCase.get(loggedInUser);

        assertEquals(1, roomViewModels.size());
        assertEquals("Test Room", roomViewModels.get(0).name);
        assertEquals(3, roomViewModels.get(0).pictureNumber);
        assertEquals(123L, roomViewModels.get(0).id);
        assertEquals("testPassword", roomViewModels.get(0).password);
        assertEquals("test.url", roomViewModels.get(0).user.get(0).userImage);
    }

    @Test
    public void givenUserHasNoImageUrl_thenReturnDefaultUrl() {
        ArrayList<Room> rooms = new ArrayList<>();
        Room room = (Room) new Room().setName("Test Room").setPassword("testPassword").setPictureNumber(3).setId(123L);
        room.getAllowedUsers().add(loggedInUser);
        rooms.add(room);
        when(roomGatewayMock.getAllAvailableForUser(loggedInUser)).thenReturn(rooms);
        loggedInUser.setPictureUrl(null);

        List<RoomViewModel> roomViewModels = getAllRoomsUseCase.get(loggedInUser);

        assertEquals(1, roomViewModels.size());
        assertEquals("Test Room", roomViewModels.get(0).name);
        assertEquals(3, roomViewModels.get(0).pictureNumber);
        assertEquals(123L, roomViewModels.get(0).id);
        assertEquals("testPassword", roomViewModels.get(0).password);
        assertEquals(DEFAULT_IMAGE_URL, roomViewModels.get(0).user.get(0).userImage);
    }

    @Test
    public void givenUserHasEmptyStringAsImageUrl_thenReturnDefaultUrl() {
        ArrayList<Room> rooms = new ArrayList<>();
        Room room = (Room) new Room().setName("Test Room").setPassword("testPassword").setPictureNumber(3).setId(123L);
        room.getAllowedUsers().add(loggedInUser);
        rooms.add(room);
        when(roomGatewayMock.getAllAvailableForUser(loggedInUser)).thenReturn(rooms);
        loggedInUser.setPictureUrl(" ");

        List<RoomViewModel> roomViewModels = getAllRoomsUseCase.get(loggedInUser);

        assertEquals(1, roomViewModels.size());
        assertEquals("Test Room", roomViewModels.get(0).name);
        assertEquals(3, roomViewModels.get(0).pictureNumber);
        assertEquals(123L, roomViewModels.get(0).id);
        assertEquals("testPassword", roomViewModels.get(0).password);
        assertEquals(DEFAULT_IMAGE_URL, roomViewModels.get(0).user.get(0).userImage);
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
                .setUser((User) new User().setNickName("Test").setId(88L))
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
        room.getAllowedUsers().add(loggedInUser);
        rooms.add(room);
        when(roomGatewayMock.getAllAvailableForUser(loggedInUser)).thenReturn(rooms);
        RoomViewModel expectedRoomViewModel = new RoomViewModel();
        expectedRoomViewModel.id = 123L;
        expectedRoomViewModel.pictureNumber = 3;
        expectedRoomViewModel.name = "Test Room";
        expectedRoomViewModel.password = "testPassword";
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
        UserViewModel userViewModel = new UserViewModel();
        userViewModel.id = loggedInUser.getId();
        userViewModel.nickName = "Test Fullname";
        userViewModel.username = "username";
        userViewModel.userImage = "test.url";
        expectedRoomViewModel.user.add(userViewModel);
        loggedInUser.setNickName("Test Fullname").setPictureUrl("test.url");

        List<RoomViewModel> roomViewModels = getAllRoomsUseCase.get(loggedInUser);

        assertThat(roomViewModels.get(0)).usingRecursiveComparison().isEqualTo(expectedRoomViewModel);
    }

    @Test
    public void givenMultipleRoomsWithAllowedUsers_thenShowThemInViewModel() throws Exception {
        Room roomWithUsers = (Room) new Room().setName("TestRoom").setPictureNumber(3).setId(123L);
        User user1 = (User) new User().setNickName("User 1").setId(312L);
        User user2 = (User) new User().setNickName("User 2").setId(352L);
        User user3 = (User) new User().setNickName("User 3").setId(398L);
        roomWithUsers.setAllowedUsers(Set.of(user1, user2, user3, loggedInUser));
        Room roomWithDifferentUsers = (Room) new Room().setName("Different Room").setPassword("password").setPictureNumber(2).setId(321L);
        User user4 = (User) new User().setNickName("User 4").setId(512L);
        User user5 = (User) new User().setNickName("User 5").setId(513L);
        User user6 = (User) new User().setNickName("User 6").setId(514L);
        roomWithDifferentUsers.setAllowedUsers(Set.of(user4, user5, user6));
        List<Room> rooms = new ArrayList<>();
        rooms.add(roomWithUsers);
        rooms.add(roomWithDifferentUsers);
        when(roomGatewayMock.getAllAvailableForUser(loggedInUser)).thenReturn(rooms);

        List<RoomViewModel> roomViewModels = getAllRoomsUseCase.get(loggedInUser);

        assertEquals(2, roomViewModels.size());
        assertEquals(4, roomViewModels.get(0).user.size());
        assertEquals(3, roomViewModels.get(1).user.size());
        assertNotNull(roomViewModels.get(0).user.get(0).nickName);
    }
}