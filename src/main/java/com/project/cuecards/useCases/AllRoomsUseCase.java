package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.AllRooms;
import com.project.cuecards.entities.Folder;
import com.project.cuecards.entities.Room;
import com.project.cuecards.entities.User;
import com.project.cuecards.gateways.RoomGateway;
import com.project.cuecards.services.PrepareDataViewModelService;
import com.project.cuecards.viewModels.DataViewModel;
import com.project.cuecards.viewModels.RoomViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AllRoomsUseCase implements AllRooms {

    private final RoomGateway roomGateway;
    private final PrepareDataViewModelService prepareDataViewModelService;
    private User loggedInUser;

    @Autowired
    public AllRoomsUseCase(RoomGateway roomGateway,
                           PrepareDataViewModelService prepareDataViewModelService) {
        this.roomGateway = roomGateway;
        this.prepareDataViewModelService = prepareDataViewModelService;
    }
    @Override
    public ArrayList<RoomViewModel> get(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        ArrayList<Room> rooms = roomGateway.getAllAvailableForUser(loggedInUser);
        ArrayList<RoomViewModel> roomViewModels = new ArrayList<>();
        for (Room room : rooms)
            getRoomViewModelFromRoom(roomViewModels, room);
        return roomViewModels;
    }

    private void getRoomViewModelFromRoom(ArrayList<RoomViewModel> roomViewModels, Room room) {
        RoomViewModel roomViewModel = new RoomViewModel();
        roomViewModel.id = room.getId();
        roomViewModel.name = room.getName();
        roomViewModel.pictureNumber = room.getPictureNumber();
        roomViewModel.data = getDataViewModelForRoom(room);
        roomViewModels.add(roomViewModel);
    }

    private DataViewModel getDataViewModelForRoom(Room room) {
        DataViewModel viewModel = prepareDataViewModelService.getFilledViewModel(getRootFoldersFromRoom(room.getFolders()), loggedInUser);
        viewModel.lastModified = room.getLastModifiedDateTime();
        return viewModel;
    }

    private List<Folder> getRootFoldersFromRoom(List<Folder> folders) {
        List<Folder> rootFolders = new ArrayList<>();
        for (Folder folder : folders) {
            if (folder.getRootFolder() == null)
                rootFolders.add(folder);
        }
        return rootFolders;
    }
}
