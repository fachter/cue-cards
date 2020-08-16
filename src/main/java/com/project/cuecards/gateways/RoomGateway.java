package com.project.cuecards.gateways;

import com.project.cuecards.entities.Room;
import com.project.cuecards.exceptions.RoomNotFoundException;

public interface RoomGateway {
    void save(Room room);

    Room getById(Long id) throws RoomNotFoundException;
}
