package com.project.cuecards.controllers;

import com.project.cuecards.boundaries.ChangeUsersProfileDataUseCase;
import com.project.cuecards.boundaries.UsersProfileDataUseCase;
import com.project.cuecards.services.LoggedInUserService;
import com.project.cuecards.viewModels.UserViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api")
public class ProfileController {

    private final UsersProfileDataUseCase usersProfileDataUseCase;
    private final ChangeUsersProfileDataUseCase changeProfileDataUseCase;

    @Autowired
    public ProfileController(UsersProfileDataUseCase usersProfileDataUseCase,
                             ChangeUsersProfileDataUseCase changeProfileDataUseCase) {
        this.usersProfileDataUseCase = usersProfileDataUseCase;
        this.changeProfileDataUseCase = changeProfileDataUseCase;
    }

    @GetMapping("/user/profile-data")
    public ResponseEntity<?> getUsersProfileData() {
        UserViewModel userViewModel = usersProfileDataUseCase
                .get(LoggedInUserService.getLoggedInUser());
        return new ResponseEntity<>(userViewModel,HttpStatus.OK);
    }

    @PostMapping("/user/change-profile-data")
    public ResponseEntity<?> changeUsersProfileData(@RequestBody UserViewModel userViewModel) {
        changeProfileDataUseCase.change(userViewModel, LoggedInUserService.getLoggedInUser());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
