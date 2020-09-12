package com.project.cuecards.gateways;

import com.project.cuecards.entities.Room;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.RoomNotFoundException;

import java.util.ArrayList;
import java.util.List;

public interface RoomGateway {
    void save(Room room);

    Room getById(Long id) throws RoomNotFoundException;

    List<Room> getAllAvailableForUser(User user);

    void deleteRoom(Room room);
}
