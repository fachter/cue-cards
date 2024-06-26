package com.project.cuecards.controllers;

import com.project.cuecards.boundaries.ChangeUsersProfileDataUseCase;
import com.project.cuecards.boundaries.UsersProfileDataUseCase;
import com.project.cuecards.exceptions.EmailAlreadyExistsException;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.exceptions.UserAlreadyExistsException;
import com.project.cuecards.exceptions.UsernameAlreadyExistsException;
import com.project.cuecards.services.LoggedInUserService;
import com.project.cuecards.viewModels.AuthenticationResponse;
import com.project.cuecards.viewModels.ChangeUserViewModel;
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
        UserViewModel userViewModel;
        try {
            userViewModel = usersProfileDataUseCase
                    .get(LoggedInUserService.getLoggedInUser());
        } catch (InvalidDataException e) {
            return new ResponseEntity<>("Invalid User", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(userViewModel, HttpStatus.OK);
    }

    @PostMapping("/user/change-profile-data")
    public ResponseEntity<?> changeUsersProfileData(@RequestBody ChangeUserViewModel userViewModel) {
        try {
            return new ResponseEntity<>(
                    changeProfileDataUseCase.change(userViewModel, LoggedInUserService.getLoggedInUser()),
                    HttpStatus.OK);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>("Username and Email already exist", HttpStatus.BAD_REQUEST);
        } catch (EmailAlreadyExistsException e) {
            return new ResponseEntity<>("Email already exists", HttpStatus.valueOf(406));
        } catch (UsernameAlreadyExistsException e) {
            return new ResponseEntity<>("Username already exists", HttpStatus.valueOf(409));
        } catch (InvalidDataException e) {
            return new ResponseEntity<>("Invalid Data", HttpStatus.valueOf(405));
        }
    }
}
