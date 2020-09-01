package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.GetRoom;
import com.project.cuecards.entities.Room;
import com.project.cuecards.exceptions.RoomHasPasswordException;
import com.project.cuecards.exceptions.RoomNotFoundException;
import com.project.cuecards.gateways.RoomGateway;
import com.project.cuecards.viewModels.RoomViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetRoomUseCaseTest {

    private GetRoom getRoom;
    @Mock private RoomGateway roomGatewayMock;

    @BeforeEach
    void setUp() {
        getRoom = new GetRoomUseCase(roomGatewayMock);
    }

    @Test
    public void givenNoRoomExistsForGivenId_thenThrowException() throws Exception {
        when(roomGatewayMock.getById(123L)).thenThrow(RoomNotFoundException.class);
        assertThrows(RoomNotFoundException.class, () -> getRoom.get(123L));
    }

    @Test
    public void givenRoomExistsWithoutPassword_thenReturnRoomViewModel() throws Exception {
        Room room = (Room) new Room()
                .setName("Test Room")
                .setId(12L);
        when(roomGatewayMock.getById(12L)).thenReturn(room);

        RoomViewModel roomViewModel = getRoom.get(12L);

        assertEquals("Test Room", roomViewModel.name);
        assertEquals(12L, roomViewModel.id);
    }

    @Test
    public void givenRoomExistsWithoutPassword_thenThrowExcpetion() throws Exception {
        Room room = (Room) new Room()
                .setName("Test Room")
                .setPassword("password")
                .setId(21L);
        when(roomGatewayMock.getById(21L)).thenReturn(room);

        assertThrows(RoomHasPasswordException.class, ()-> getRoom.get(21L));
    }
}