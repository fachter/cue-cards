package com.project.cuecards.boundaries;

import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.viewModels.RoomViewModel;

import java.util.List;

public interface GetRoomsWebUseCase {
    List<RoomViewModel> get(User loggedInUser) throws InvalidDataException;
}
