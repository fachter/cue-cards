package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.AddOrEditRoom;
import com.project.cuecards.entities.Room;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.exceptions.RoomNotFoundException;
import com.project.cuecards.gateways.RoomGateway;
import com.project.cuecards.viewModels.RoomViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddOrEditRoomUseCase implements AddOrEditRoom {

    private final RoomGateway roomGateway;
    private User loggedInUser;

    @Autowired
    public AddOrEditRoomUseCase(RoomGateway roomGateway) {
        this.roomGateway = roomGateway;
    }

    @Override
    public void add(RoomViewModel roomViewModel, User loggedInUser) throws InvalidArgumentException {
        if (roomViewModel == null || loggedInUser == null)
            throw new InvalidArgumentException();
        this.loggedInUser = loggedInUser;
        Room room = getRoom(roomViewModel)
                .setName(roomViewModel.name)
                .setPassword(getPassword(roomViewModel));
        roomGateway.save(room);
    }

    private String getPassword(RoomViewModel roomViewModel) {
        if (roomViewModel.password == null || roomViewModel.password.equals(""))
            return null;
        return roomViewModel.password;
    }

    private Room getRoom(RoomViewModel roomViewModel) {
        try {
            if (roomViewModel.id != null)
                return roomGateway.getById(roomViewModel.id);
        } catch (RoomNotFoundException ignored) {}
        Room room = new Room();
        room.getAllowedUsers().add(loggedInUser);
        return room;
    }
}
