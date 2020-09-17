package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.AuthenticateToRoom;
import com.project.cuecards.entities.Room;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.exceptions.RoomNotFoundException;
import com.project.cuecards.gateways.RoomGateway;
import com.project.cuecards.viewModels.RoomViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticateToRoomUseCaseTest {

    private AuthenticateToRoom authenticateToRoom;
    @Mock private RoomGateway roomGatewayMock;
    private final User loggedInUser = new User().setNickName("Felix").setUsername("fachter").setPassword("password");

    @BeforeEach
    void setUp() {
        authenticateToRoom = new AuthenticateToRoomUseCase(roomGatewayMock);
    }

    @Test
    public void givenRoomIdDoesNotExist_thenThrowException() throws Exception {
        RoomViewModel viewModel = new RoomViewModel();
        viewModel.id = 123L;
        viewModel.password = "test";
        when(roomGatewayMock.getById(123L)).thenThrow(new RoomNotFoundException());

        assertThrows(RoomNotFoundException.class, ()->authenticateToRoom.authenticate(viewModel, loggedInUser));
    }

    @Test
    public void givenRoomExistsAndPasswordIsWrong_thenThrowException() throws Exception {
        RoomViewModel viewModel = new RoomViewModel();
        viewModel.id = 123L;
        viewModel.password = "wrong";
        Room room = (Room) new Room().setPassword("password").setName("room").setId(123L);
        when(roomGatewayMock.getById(123L)).thenReturn(room);

        assertThrows(InvalidDataException.class, ()->authenticateToRoom.authenticate(viewModel, loggedInUser));
    }

    @Test
    public void givenRoomExistsAndCorrectPassword_thenJoin() throws Exception {
        RoomViewModel viewModel = new RoomViewModel();
        viewModel.id = 123L;
        viewModel.password = "password";
        Room room = (Room) new Room().setPassword("password").setName("room").setId(123L);
        when(roomGatewayMock.getById(123L)).thenReturn(room);

        authenticateToRoom.authenticate(viewModel, loggedInUser);

        verify(roomGatewayMock, times(1)).save(room);
        assertEquals(1, room.getAllowedUsers().size());
        assertTrue(room.getAllowedUsers().contains(loggedInUser));
    }

    @Test
    public void givenRoomExistsWithLoggedInUser_thenDoNotAddUserAgain() throws Exception {
        RoomViewModel viewModel = new RoomViewModel();
        viewModel.id = 123L;
        viewModel.password = "password";
        Room room = (Room) new Room().setPassword("password").setName("room").setId(123L);
        room.getAllowedUsers().add(loggedInUser);
        when(roomGatewayMock.getById(123L)).thenReturn(room);

        authenticateToRoom.authenticate(viewModel, loggedInUser);

        verify(roomGatewayMock, times(1)).save(room);
        assertEquals(1, room.getAllowedUsers().size());
        assertTrue(room.getAllowedUsers().contains(loggedInUser));
    }
}