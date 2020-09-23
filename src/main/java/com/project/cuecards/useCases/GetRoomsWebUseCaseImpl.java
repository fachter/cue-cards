package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.GetRoomsWebUseCase;
import com.project.cuecards.entities.Room;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.services.GetUserRoomsService;
import com.project.cuecards.services.UsersHomeDataServiceImpl;
import com.project.cuecards.viewModels.RoomViewModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetRoomsWebUseCaseImpl implements GetRoomsWebUseCase {

    private final UsersHomeDataServiceImpl usersHomeDataService;
    private final GetUserRoomsService getUserRoomsService;

    public GetRoomsWebUseCaseImpl(UsersHomeDataServiceImpl usersHomeDataService, GetUserRoomsService getUserRoomsService) {
        this.usersHomeDataService = usersHomeDataService;
        this.getUserRoomsService = getUserRoomsService;
    }

    @Override
    public List<RoomViewModel> get(User loggedInUser) throws InvalidDataException {
        if (loggedInUser == null)
            throw new InvalidDataException();
        List<RoomViewModel> roomViewModels = getUserRoomsService.get(loggedInUser);
        roomViewModels.add(0, getMeinRaum(loggedInUser));
        return roomViewModels;
    }

    private RoomViewModel getMeinRaum(User loggedInUser) {
        RoomViewModel roomViewModel = new RoomViewModel();
        roomViewModel.id = 0L;
        roomViewModel.name = "Mein Raum";
        roomViewModel.data = usersHomeDataService.get(loggedInUser);
        return roomViewModel;
    }
}
