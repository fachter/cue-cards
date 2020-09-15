package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.GetAllRoomsUseCase;
import com.project.cuecards.entities.Folder;
import com.project.cuecards.entities.Room;
import com.project.cuecards.entities.User;
import com.project.cuecards.gateways.RoomGateway;
import com.project.cuecards.services.GetUserRoomsService;
import com.project.cuecards.services.PrepareDataViewModelService;
import com.project.cuecards.viewModels.DataViewModel;
import com.project.cuecards.viewModels.RoomViewModel;
import com.project.cuecards.viewModels.UserViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetAllRoomsUseCaseImpl implements GetAllRoomsUseCase {

    private final GetUserRoomsService getUserRoomsService;

    @Autowired
    public GetAllRoomsUseCaseImpl(GetUserRoomsService getUserRoomsService) {
        this.getUserRoomsService = getUserRoomsService;
    }

    @Override
    public List<RoomViewModel> get(User loggedInUser) {
        return getUserRoomsService.get(loggedInUser);
    }
}
