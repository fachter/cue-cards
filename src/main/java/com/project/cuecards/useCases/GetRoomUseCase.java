package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.GetRoom;
import com.project.cuecards.entities.Room;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.RoomHasPasswordException;
import com.project.cuecards.exceptions.RoomNotFoundException;
import com.project.cuecards.gateways.RoomGateway;
import com.project.cuecards.viewModels.RoomViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetRoomUseCase implements GetRoom {

    private final RoomGateway roomGateway;

    @Autowired
    public GetRoomUseCase(RoomGateway roomGateway) {
        this.roomGateway = roomGateway;
    }

    @Override
    public RoomViewModel join(Long roomId, User loggedInUser) throws RoomNotFoundException, RoomHasPasswordException {
        Room room = roomGateway.getById(roomId);
        if (room.getPassword() != null)
            throw new RoomHasPasswordException(room.getName());
        joinRoom(loggedInUser, room);
        return getRoomViewModelFromRoom(room);
    }

    private void joinRoom(User loggedInUser, Room room) {
        room.getAllowedUsers().add(loggedInUser);
        roomGateway.save(room);
    }

    private RoomViewModel getRoomViewModelFromRoom(Room room) {
        RoomViewModel roomViewModel = new RoomViewModel();
        roomViewModel.id = room.getId();
        roomViewModel.name = room.getName();
        return roomViewModel;
    }
}
