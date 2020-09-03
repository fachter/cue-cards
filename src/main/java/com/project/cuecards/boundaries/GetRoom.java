package com.project.cuecards.boundaries;

import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.RoomHasPasswordException;
import com.project.cuecards.exceptions.RoomNotFoundException;
import com.project.cuecards.viewModels.RoomViewModel;

public interface GetRoom {

    RoomViewModel join(Long roomId, User loggedInUser) throws RoomNotFoundException, RoomHasPasswordException;
}
