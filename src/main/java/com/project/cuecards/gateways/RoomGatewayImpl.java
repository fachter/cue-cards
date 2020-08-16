package com.project.cuecards.gateways;

import com.project.cuecards.entities.Room;
import com.project.cuecards.exceptions.RoomNotFoundException;
import com.project.cuecards.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoomGatewayImpl implements RoomGateway {

    private final RoomRepository roomRepository;

    @Autowired
    public RoomGatewayImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public void save(Room room) {
        roomRepository.save(room);
    }

    @Override
    public Room getById(Long id) throws RoomNotFoundException {
        Optional<Room> room = roomRepository.findById(id);
        if (room.isEmpty())
            throw new RoomNotFoundException();
        return room.get();
    }
}
