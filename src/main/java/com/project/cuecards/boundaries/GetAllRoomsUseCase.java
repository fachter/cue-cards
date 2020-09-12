package com.project.cuecards.boundaries;

import com.project.cuecards.entities.User;
import com.project.cuecards.viewModels.RoomViewModel;

import java.util.ArrayList;
import java.util.List;

public interface GetAllRoomsUseCase {

    List<RoomViewModel> get(User loggedInUser);
}
