package com.project.cuecards.boundaries;

import com.project.cuecards.entities.User;
import com.project.cuecards.viewModels.RoomViewModel;

import java.util.ArrayList;

public interface AllRooms {

    ArrayList<RoomViewModel> get(User loggedInUser);
}
