package com.project.cuecards.controllers;

import com.project.cuecards.boundaries.*;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.exceptions.RoomHasPasswordException;
import com.project.cuecards.exceptions.RoomNotFoundException;
import com.project.cuecards.services.LoggedInUserService;
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
    private final GetRoom getRoom;
    private final DeleteRooms deleteRooms;

    public RoomController(AllRooms allRooms,
                          AddOrEditRoom addOrEditRoom,
                          JoinRoom joinRoom,
                          GetRoom getRoom,
                          DeleteRooms deleteRooms) {
        this.allRooms = allRooms;
        this.addOrEditRoom = addOrEditRoom;
        this.joinRoom = joinRoom;
        this.getRoom = getRoom;
        this.deleteRooms = deleteRooms;
    }

    @GetMapping("/api/get-available-rooms")
    public ResponseEntity<?> getAllRooms() {
        ArrayList<RoomViewModel> rooms = allRooms.get(LoggedInUserService.getLoggedInUser());
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @PostMapping("/api/delete-rooms")
    public ResponseEntity<?> deleteRoom(@RequestBody RoomViewModel[] roomViewModels) {
        deleteRooms.delete(roomViewModels);
        return new ResponseEntity<>("Raum / Räume deleted", HttpStatus.OK);
    }

    @PostMapping("/api/room")
    public ResponseEntity<?> addRoom(@RequestBody RoomViewModel roomViewModel) {
        try {
            addOrEditRoom.add(roomViewModel, LoggedInUserService.getLoggedInUser());
        } catch (InvalidArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Object mit Passwort zurück schicken
    @GetMapping("/api/get-room/{roomId}")
    public ResponseEntity<?> joinRoom(@PathVariable Long roomId) {
        try {
            return new ResponseEntity<>(getRoom.get(roomId), HttpStatus.OK);
        } catch (RoomHasPasswordException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.valueOf(202));
        } catch (RoomNotFoundException roomNotFoundException) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
