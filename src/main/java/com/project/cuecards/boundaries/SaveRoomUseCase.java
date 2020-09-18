package com.project.cuecards.boundaries;

import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.viewModels.RoomViewModel;

public interface SaveRoomUseCase {

    void save(RoomViewModel roomViewModel, User loggedInUser) throws InvalidArgumentException, InvalidDataException;
}
