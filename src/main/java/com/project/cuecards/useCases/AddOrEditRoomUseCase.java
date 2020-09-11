package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.AddOrEditRoom;
import com.project.cuecards.entities.Room;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.exceptions.RoomNotFoundException;
import com.project.cuecards.gateways.RoomGateway;
import com.project.cuecards.services.SaveDataViewModelServiceImpl;
import com.project.cuecards.viewModels.RoomViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddOrEditRoomUseCase implements AddOrEditRoom {

    private final RoomGateway roomGateway;
    private final SaveDataViewModelServiceImpl saveDataViewModelService;
    private User loggedInUser;

    @Autowired
    public AddOrEditRoomUseCase(RoomGateway roomGateway,
                                SaveDataViewModelServiceImpl saveDataViewModelService) {
        this.roomGateway = roomGateway;
        this.saveDataViewModelService = saveDataViewModelService;
    }

    @Override
    public void add(RoomViewModel roomViewModel, User loggedInUser) throws InvalidArgumentException, InvalidDataException {
        if (roomViewModel == null || loggedInUser == null)
            throw new InvalidArgumentException();
        this.loggedInUser = loggedInUser;
        Room room = getRoom(roomViewModel)
                .setName(roomViewModel.name)
                .setPassword(getPassword(roomViewModel))
                .setPictureNumber(roomViewModel.pictureNumber);
        saveRoomData(roomViewModel, loggedInUser, room);
        roomGateway.save(room);
    }

    private void saveRoomData(RoomViewModel roomViewModel, User loggedInUser, Room room) throws InvalidDataException {
        if (roomViewModel.data != null) {
            saveDataViewModelService.setRoom(room);
            saveDataViewModelService.saveOrUpdateDataViewModel(roomViewModel.data, room.getFolders(), loggedInUser);
            room.setLastModifiedDateTime(roomViewModel.data.lastModified);
        }
    }

    private String getPassword(RoomViewModel roomViewModel) {
        if (roomViewModel.password == null || roomViewModel.password.equals(""))
            return null;
        return roomViewModel.password;
    }

    private Room getRoom(RoomViewModel roomViewModel) {
        try {
            if (roomViewModel.id != null)
                return roomGateway.getById(roomViewModel.id);
        } catch (RoomNotFoundException ignored) {
        }
        Room room = new Room();
        room.getAllowedUsers().add(loggedInUser);
        return room;
    }
}
