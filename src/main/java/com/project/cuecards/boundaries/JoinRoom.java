package com.project.cuecards.boundaries;

import com.project.cuecards.entities.User;
import com.project.cuecards.viewModels.JoinRoomViewModel;

public interface JoinRoom {

    void join(JoinRoomViewModel joinRoomViewModel, User loggedInUser);
}
