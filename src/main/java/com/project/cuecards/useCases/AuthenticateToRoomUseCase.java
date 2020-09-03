package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.AuthenticateToRoom;
import com.project.cuecards.entities.Room;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.exceptions.RoomNotFoundException;
import com.project.cuecards.gateways.RoomGateway;
import com.project.cuecards.viewModels.RoomViewModel;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateToRoomUseCase implements AuthenticateToRoom {

    private final RoomGateway roomGateway;

    public AuthenticateToRoomUseCase(RoomGateway roomGateway) {
        this.roomGateway = roomGateway;
    }

    @Override
    public void authenticate(RoomViewModel viewModel, User loggedInUser) throws RoomNotFoundException, InvalidDataException {
        Room room = roomGateway.getById(viewModel.id);
        if (!room.getPassword().equals(viewModel.password))
            throw new InvalidDataException();
        room.getAllowedUsers().add(loggedInUser);
        roomGateway.save(room);
    }
}
