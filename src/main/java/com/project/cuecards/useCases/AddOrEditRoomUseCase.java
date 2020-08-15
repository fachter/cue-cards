package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.AddOrEditRoom;
import com.project.cuecards.entities.User;
import com.project.cuecards.viewModels.RoomViewModel;
import org.springframework.stereotype.Service;

@Service
public class AddOrEditRoomUseCase implements AddOrEditRoom {

    @Override
    public void add(RoomViewModel roomViewModel, User loggedInUser) {

    }
}
