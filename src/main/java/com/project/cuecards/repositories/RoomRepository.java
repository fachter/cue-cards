package com.project.cuecards.repositories;

import com.project.cuecards.entities.Room;
import com.project.cuecards.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findAllByAllowedUsersContains(User user);
}
