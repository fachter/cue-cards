package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.LeaveRoomUseCase;
import com.project.cuecards.entities.Room;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.RoomNotFoundException;
import com.project.cuecards.gateways.RoomGateway;
import com.project.cuecards.gateways.UserGateway;
import org.springframework.stereotype.Service;

@Service
public class LeaveRoomUseCaseImpl implements LeaveRoomUseCase {
    private final RoomGateway roomGateway;
    private final UserGateway userGateway;

    public LeaveRoomUseCaseImpl(RoomGateway roomGateway, UserGateway userGateway) {
        this.roomGateway = roomGateway;
        this.userGateway = userGateway;
    }

    @Override
    public void leave(Long roomId, User loggedInUser) throws RoomNotFoundException {
        Room room = roomGateway.getById(roomId);
        for (User user : room.getAllowedUsers()) {
            if (user.getId().equals(loggedInUser.getId())) {
                user.getAvailableRooms().remove(room);
                room.getAllowedUsers().remove(user);
            }
        }
        if (room.getAllowedUsers().size() == 0)
            roomGateway.deleteRoom(room);
        else
            roomGateway.save(room);
    }
}
