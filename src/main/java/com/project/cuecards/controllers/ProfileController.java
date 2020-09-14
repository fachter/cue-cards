package com.project.cuecards.controllers;

import com.project.cuecards.viewModels.UserViewModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api")
public class ProfileController {

    @GetMapping("/user/profile-data")
    public ResponseEntity<?> getUsersProfileData() {
        UserViewModel userViewModel = new UserViewModel();
        userViewModel.name = "Test User";
        userViewModel.pictureUrl = "testUrl";
        return new ResponseEntity<>(userViewModel,HttpStatus.OK);
    }

    @PostMapping("/user/change-profile-data")
    public ResponseEntity<?> changeUsersProfileData() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
