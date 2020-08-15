package com.project.cuecards.boundaries;

import com.project.cuecards.entities.User;
import com.project.cuecards.viewModels.RoomViewModel;

public interface AddOrEditRoom {

    void add(RoomViewModel roomViewModel, User loggedInUser);
}
