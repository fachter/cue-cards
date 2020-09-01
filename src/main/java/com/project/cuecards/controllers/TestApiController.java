package com.project.cuecards.controllers;

import com.project.cuecards.entities.User;
import com.project.cuecards.services.LoggedInUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestApiController {

    @GetMapping("/api/")
    public ResponseEntity<String> testFunction() {
        return new ResponseEntity<>("Test", HttpStatus.OK);
    }

    @GetMapping("/api/getAvailableRooms")
    public ResponseEntity<?> getRoomsForUser() {
        User user = LoggedInUserService.getLoggedInUser();
        return new ResponseEntity<>(user.getAvailableRooms(), HttpStatus.ACCEPTED);
    }
}
