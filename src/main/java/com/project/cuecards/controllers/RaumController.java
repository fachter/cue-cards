package com.project.cuecards.controllers;

import com.project.cuecards.viewModels.DataViewModel;
import com.project.cuecards.viewModels.FolderViewModel;
import com.project.cuecards.viewModels.RoomViewModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;

public class RaumController {

    @GetMapping("/get-available-rooms")
    public ResponseEntity<?> getAllRooms() {
        ArrayList<RoomViewModel> rooms = new ArrayList<>();
        return new ResponseEntity<>(rooms,HttpStatus.OK);
    }

    @PostMapping("/add-room")
    public ResponseEntity<?> addRoom() {
        RoomViewModel roomViewModel = new RoomViewModel();
        roomViewModel.id = 1L;
        roomViewModel.name = "Test Room";
        roomViewModel.data = new DataViewModel();
        FolderViewModel testFolder = new FolderViewModel();
        testFolder.name = "Test Folder";
        testFolder.ID = "Test Folders Id";
        roomViewModel.data.folders.add(testFolder);
        return new ResponseEntity<>(roomViewModel, HttpStatus.OK);
    }

    @PostMapping("/delete-rooms")
    public ResponseEntity<?> deleteRoom() {
        return new ResponseEntity<>("Raum / Räume deleted", HttpStatus.OK);
    }

    @PostMapping("/edit-room")
    public ResponseEntity<?> editRoom() {
        return new ResponseEntity<>("Room changed", HttpStatus.OK);
    }


}
