package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.AllRooms;
import com.project.cuecards.entities.User;
import com.project.cuecards.viewModels.RoomViewModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AllRoomsUseCase implements AllRooms {
    @Override
    public ArrayList<RoomViewModel> get(User loggedInUser) {
        return null;
    }
}
