package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.JoinRoom;
import com.project.cuecards.entities.User;
import com.project.cuecards.viewModels.JoinRoomViewModel;
import org.springframework.stereotype.Service;

@Service
public class JoinRoomUseCase implements JoinRoom {

    @Override
    public void join(JoinRoomViewModel joinRoomViewModel, User loggedInUser) {

    }
}
