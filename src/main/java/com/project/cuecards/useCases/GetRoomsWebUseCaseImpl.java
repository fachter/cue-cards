package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.GetRoomsWebUseCase;
import com.project.cuecards.entities.User;
import com.project.cuecards.viewModels.RoomViewModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRoomsWebUseCaseImpl implements GetRoomsWebUseCase {
    @Override
    public List<RoomViewModel> get(User loggedInUser) {
        return null;
    }
}
