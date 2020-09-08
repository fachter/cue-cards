package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.AllRooms;
import com.project.cuecards.entities.Room;
import com.project.cuecards.entities.User;
import com.project.cuecards.gateways.RoomGateway;
import com.project.cuecards.viewModels.RoomViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AllRoomsUseCaseTest {

    private AllRooms allRooms;
    @Mock private RoomGateway roomGatewayMock;
    private final User loggedInUser = new User()
            .setUsername("username").setPassword("password");

    @BeforeEach
    void setUp() {
        allRooms = new AllRoomsUseCase(roomGatewayMock);
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
        assertEquals("testPassword", roomViewModels.get(0).password);
        assertEquals(3, roomViewModels.get(0).pictureNumber);
        assertEquals(123L, roomViewModels.get(0).id);
    }
}