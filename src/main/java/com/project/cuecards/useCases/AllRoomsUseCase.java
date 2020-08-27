package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.AllRooms;
import com.project.cuecards.entities.Room;
import com.project.cuecards.entities.User;
import com.project.cuecards.gateways.RoomGateway;
import com.project.cuecards.viewModels.RoomViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AllRoomsUseCase implements AllRooms {

    private final RoomGateway roomGateway;

    @Autowired
    public AllRoomsUseCase(RoomGateway roomGateway) {
        this.roomGateway = roomGateway;
    }
    @Override
    public ArrayList<RoomViewModel> get(User loggedInUser) {
        ArrayList<Room> rooms = roomGateway.getAllAvailableForUser(loggedInUser);
        ArrayList<RoomViewModel> roomViewModels = new ArrayList<>();
        for (Room room : rooms) {
            RoomViewModel roomViewModel = new RoomViewModel();
            roomViewModel.id = room.getId();
            roomViewModel.name = room.getName();
            roomViewModel.password = room.getPassword();
            roomViewModels.add(roomViewModel);
        }
        return roomViewModels;
    }
}
