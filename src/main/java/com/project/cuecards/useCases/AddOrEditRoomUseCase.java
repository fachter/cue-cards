package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.AddOrEditRoom;
import com.project.cuecards.entities.Room;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.exceptions.RoomNotFoundException;
import com.project.cuecards.gateways.RoomGateway;
import com.project.cuecards.viewModels.RoomViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AddOrEditRoomUseCase implements AddOrEditRoom {

    private final RoomGateway roomGateway;
    private final PasswordEncoder passwordEncoder;
    private User loggedInUser;

    @Autowired
    public AddOrEditRoomUseCase(RoomGateway roomGateway, PasswordEncoder passwordEncoder) {
        this.roomGateway = roomGateway;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void add(RoomViewModel roomViewModel, User loggedInUser) throws InvalidArgumentException {
        if (roomViewModel == null || loggedInUser == null)
            throw new InvalidArgumentException();
        this.loggedInUser = loggedInUser;
        Room room = getRoom(roomViewModel)
                .setName(roomViewModel.name)
                .setPassword(passwordEncoder.encode(roomViewModel.password));
        roomGateway.save(room);
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
