package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.LeaveRoomUseCase;
import com.project.cuecards.entities.Room;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.RoomNotFoundException;
import com.project.cuecards.gateways.RoomGateway;
import com.project.cuecards.gateways.UserGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.util.Types;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveRoomUseCaseImplTest {

    private LeaveRoomUseCase useCase;
    @Mock private RoomGateway roomGatewayMock;
    @Mock private UserGateway userGatewayMock;
    private final User loggedInUser = (User) new User()
            .setUsername("test").setPassword("test").setId(1L);
    private final User passedUser = (User) new User().setId(1L);
    @Captor private ArgumentCaptor<Room> captor;

    @BeforeEach
    void setUp() {
        useCase = new LeaveRoomUseCaseImpl(roomGatewayMock, userGatewayMock);
    }

    @Test
    public void givenRoomIdDoesNotExist_thenThrowException() throws Exception {
        when(roomGatewayMock.getById(123L)).thenThrow(RoomNotFoundException.class);
        assertThrows(RoomNotFoundException.class, () -> useCase.leave(123L, new User()));
    }

    @Test
    public void givenRoomExistsWithUserInSet_thenDeleteRoomFromUserAndUserFromRoom() throws Exception {
        Room testRoom = (Room) new Room().setName("TestRoom").setPassword("password")
                .setPictureNumber(3).setId(321L);
        testRoom.getAllowedUsers().add(loggedInUser);
        testRoom.getAllowedUsers().add((User) new User().setUsername("other User").setId(5L));
        loggedInUser.getAvailableRooms().add(testRoom);
        when(roomGatewayMock.getById(321L)).thenReturn(testRoom);

        useCase.leave(321L, passedUser);

        assertFalse(testRoom.getAllowedUsers().contains(loggedInUser));
        assertFalse(loggedInUser.getAvailableRooms().contains(testRoom));
        verify(roomGatewayMock, times(1)).save(testRoom);
        verify(roomGatewayMock, times(0)).deleteRoom(any());
    }

    @Test
    public void givenRoomHasNoUsersLeft_thenDeleteRoom() throws Exception {
        Room testRoom = (Room) new Room().setName("TestRoom").setPassword("password")
                .setPictureNumber(3).setId(321L);
        testRoom.getAllowedUsers().add(loggedInUser);
        loggedInUser.getAvailableRooms().add(testRoom);
        when(roomGatewayMock.getById(321L)).thenReturn(testRoom);

        useCase.leave(321L, passedUser);

        verify(roomGatewayMock, times(1)).deleteRoom(testRoom);
        verify(roomGatewayMock, times(0)).save(any());
    }
}