package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.AddOrEditRoom;
import com.project.cuecards.entities.Room;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.gateways.RoomGateway;
import com.project.cuecards.viewModels.RoomViewModel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddOrEditRoomUseCaseTest {

    @Mock private RoomGateway roomGatewayMock;
    private AddOrEditRoom roomUseCase;
    private final User loggedInUser = new User().setFullName("Test").setUsername("test").setPassword("test");
    @Captor private ArgumentCaptor<Room> captor;

    @BeforeEach
    void setUp() {
        roomUseCase = new AddOrEditRoomUseCase(roomGatewayMock);
    }

    @Test
    public void givenRoomViewModelIsNull_thenThrowException() {
        assertThrows(InvalidArgumentException.class, () -> roomUseCase.add(null, new User()));
    }

    @Test
    public void givenUserIsNull_thenThrowException() {
        assertThrows(InvalidArgumentException.class, () -> roomUseCase.add(new RoomViewModel(), null));
    }

    @Test
    public void givenRoomWithoutId_thenSaveNewRoom() throws Exception {
        RoomViewModel viewModel = new RoomViewModel();
        viewModel.name = "Room";
        Room expectedRoom = new Room().setName("Room");

        roomUseCase.add(viewModel, loggedInUser);

        verify(roomGatewayMock, times(1)).save(captor.capture());
        Assertions.assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(expectedRoom);
    }

    @Test
    public void givenRoomWithId_thenChangeRoom() throws Exception {
        RoomViewModel viewModel = new RoomViewModel();
        viewModel.id = 123L;
        viewModel.name = "new Name";
        Room existingRoom = (Room) new Room().setName("old Name").setId(123L);
        when(roomGatewayMock.getById(123L)).thenReturn(existingRoom);

        roomUseCase.add(viewModel, loggedInUser);

        verify(roomGatewayMock, times(1)).save(captor.capture());
        assertEquals(existingRoom, captor.getValue());
        assertEquals("new Name", existingRoom.getName());
    }
}