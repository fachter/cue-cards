package com.project.cuecards.gateways;

import com.project.cuecards.entities.Room;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.RoomNotFoundException;

import java.util.ArrayList;

public interface RoomGateway {
    void save(Room room);

    Room getById(Long id) throws RoomNotFoundException;

    ArrayList<Room> getAllAvailableForUser(User user);
}
