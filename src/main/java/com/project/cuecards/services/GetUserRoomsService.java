package com.project.cuecards.services;

import com.project.cuecards.entities.User;
import com.project.cuecards.viewModels.RoomViewModel;

import java.util.List;

public interface GetUserRoomsService {

    List<RoomViewModel> get(User loggedInUser);
}
