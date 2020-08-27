package com.project.cuecards.controllers;

import com.project.cuecards.boundaries.AddOrEditRoom;
import com.project.cuecards.boundaries.AllRooms;
import com.project.cuecards.boundaries.DeleteRooms;
import com.project.cuecards.boundaries.JoinRoom;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.services.LoggedInUserService;
import com.project.cuecards.viewModels.JoinRoomViewModel;
import com.project.cuecards.viewModels.RoomViewModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class RoomController {

    private final AllRooms allRooms;
    private final AddOrEditRoom addOrEditRoom;
    private final JoinRoom joinRoom;
    private final DeleteRooms deleteRooms;

    public RoomController(AllRooms allRooms,
                          AddOrEditRoom addOrEditRoom,
                          JoinRoom joinRoom,
                          DeleteRooms deleteRooms) {
        this.allRooms = allRooms;
        this.addOrEditRoom = addOrEditRoom;
        this.joinRoom = joinRoom;
        this.deleteRooms = deleteRooms;
    }

    @GetMapping("/get-available-rooms")
    public ResponseEntity<?> getAllRooms() {
        ArrayList<RoomViewModel> rooms = allRooms.get(LoggedInUserService.getLoggedInUser());
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @PostMapping("/delete-rooms")
    public ResponseEntity<?> deleteRoom(@RequestBody RoomViewModel[] roomViewModels) {
        deleteRooms.delete(roomViewModels);
        return new ResponseEntity<>("Raum / Räume deleted", HttpStatus.OK);
    }

    @PostMapping("/room")
    public ResponseEntity<?> addRoom(@RequestBody RoomViewModel roomViewModel) {
        try {
            addOrEditRoom.add(roomViewModel, LoggedInUserService.getLoggedInUser());
        } catch (InvalidArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Object mit Passwort zurück schicken
    @GetMapping("/join-room/{roomId}")
    public ResponseEntity<?> joinRoom(@PathVariable Long roomId) {
        joinRoom.join(roomId, LoggedInUserService.getLoggedInUser());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
