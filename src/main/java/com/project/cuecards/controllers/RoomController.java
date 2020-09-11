package com.project.cuecards.controllers;

import com.project.cuecards.boundaries.*;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.exceptions.InvalidDataException;
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

    private final GetAllRoomsUseCase getAllRoomsUseCase;
    private final AddOrEditRoom addOrEditRoom;
    private final AuthenticateToRoom authenticateToRoom;
    private final GetRoom getRoom;
    private final LeaveRoomUseCase leaveRoomUseCase;

    public RoomController(GetAllRoomsUseCase getAllRoomsUseCase,
                          AddOrEditRoom addOrEditRoom,
                          AuthenticateToRoom authenticateToRoom,
                          GetRoom getRoom,
                          LeaveRoomUseCase leaveRoomUseCase) {
        this.getAllRoomsUseCase = getAllRoomsUseCase;
        this.addOrEditRoom = addOrEditRoom;
        this.authenticateToRoom = authenticateToRoom;
        this.getRoom = getRoom;
        this.leaveRoomUseCase = leaveRoomUseCase;
    }

    @GetMapping("/api/get-available-rooms")
    public ResponseEntity<?> getAllRooms() {
        ArrayList<RoomViewModel> rooms = getAllRoomsUseCase.get(LoggedInUserService.getLoggedInUser());
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @PostMapping("/api/leave-room/{roomId}")
    public ResponseEntity<?> deleteRoom(@PathVariable String roomId) {
        try {
            leaveRoomUseCase.leave(Long.valueOf(roomId), LoggedInUserService.getLoggedInUser());
        } catch (RoomNotFoundException roomNotFoundException) {
            roomNotFoundException.printStackTrace();
        }
        return new ResponseEntity<>("Raum verlassen", HttpStatus.OK);
    }

    @PostMapping("/api/room")
    public ResponseEntity<?> addRoom(@RequestBody RoomViewModel roomViewModel) {
        try {
            addOrEditRoom.add(roomViewModel, LoggedInUserService.getLoggedInUser());
        } catch (InvalidArgumentException | InvalidDataException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/api/join-room/authenticate")
    public ResponseEntity<?> authenticateToRoom(@RequestBody RoomViewModel viewModel) {
        try {
            authenticateToRoom.authenticate(viewModel, LoggedInUserService.getLoggedInUser());
        } catch (InvalidDataException e) {
            return new ResponseEntity<>("Wrong Password", HttpStatus.BAD_REQUEST);
        } catch (RoomNotFoundException roomNotFoundException) {
            return new ResponseEntity<>("No such Room", HttpStatus.valueOf(404));
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/join-room/{roomId}")
    public ResponseEntity<?> joinRoom(@PathVariable Long roomId) {
        try {
            return new ResponseEntity<>(getRoom.join(roomId, LoggedInUserService.getLoggedInUser()),
                    HttpStatus.OK);
        } catch (RoomHasPasswordException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.valueOf(202));
        } catch (RoomNotFoundException roomNotFoundException) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
