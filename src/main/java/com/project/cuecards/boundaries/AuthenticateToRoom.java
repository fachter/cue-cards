package com.project.cuecards.boundaries;

import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.exceptions.RoomNotFoundException;
import com.project.cuecards.viewModels.JoinRoomViewModel;
import com.project.cuecards.viewModels.RoomViewModel;

public interface AuthenticateToRoom {

    void authenticate(RoomViewModel viewModel, User loggedInUser) throws RoomNotFoundException, InvalidDataException;
}
