package com.project.cuecards.boundaries;

import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.RoomNotFoundException;

public interface LeaveRoomUseCase {
    void leave(Long roomId, User loggedInUser) throws RoomNotFoundException;
}
