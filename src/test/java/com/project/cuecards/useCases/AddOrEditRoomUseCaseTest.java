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
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddOrEditRoomUseCaseTest {

    @Mock private RoomGateway roomGatewayMock;
    @Mock private PasswordEncoder passwordEncoderMock;
    private AddOrEditRoom roomUseCase;
    private final User loggedInUser = new User().setFullName("Test").setUsername("test").setPassword("test");
    @Captor private ArgumentCaptor<Room> captor;

    @BeforeEach
    void setUp() {
        roomUseCase = new AddOrEditRoomUseCase(roomGatewayMock, passwordEncoderMock);
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
        when(passwordEncoderMock.encode("password")).thenReturn("123password%!");
        RoomViewModel viewModel = new RoomViewModel();
        viewModel.name = "Room";
        viewModel.password = "password";
        Room expectedRoom = new Room().setName("Room").setPassword("123password%!");
        expectedRoom.getAllowedUsers().add(loggedInUser);
        loggedInUser.getAvailableRooms().add(expectedRoom);

        roomUseCase.add(viewModel, loggedInUser);

        verify(roomGatewayMock, times(1)).save(captor.capture());
        Assertions.assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(expectedRoom);
    }

    @Test
    public void givenRoomWithId_thenChangeRoom() throws Exception {
        when(passwordEncoderMock.encode("newPassword")).thenReturn("123password%!");
        RoomViewModel viewModel = new RoomViewModel();
        viewModel.id = 123L;
        viewModel.name = "new Name";
        viewModel.password = "newPassword";
        Room existingRoom = (Room) new Room().setName("old Name").setPassword("oldPassword").setId(123L);
        when(roomGatewayMock.getById(123L)).thenReturn(existingRoom);

        roomUseCase.add(viewModel, loggedInUser);

        verify(roomGatewayMock, times(1)).save(captor.capture());
        assertEquals(existingRoom, captor.getValue());
        assertEquals("new Name", existingRoom.getName());
        assertEquals("123password%!", existingRoom.getPassword());
    }
}