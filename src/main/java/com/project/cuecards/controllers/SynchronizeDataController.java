package com.project.cuecards.controllers;

import com.project.cuecards.boundaries.GetUsersHomeData;
import com.project.cuecards.boundaries.SaveUsersHomeData;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.exceptions.UserDoesNotExistException;
import com.project.cuecards.utils.JwtUtil;
import com.project.cuecards.viewModels.DataViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SynchronizeDataController {

    private final GetUsersHomeData getUsersHomeData;
    private final JwtUtil jwtUtil;
    private final SaveUsersHomeData saveUsersHomeData;

    @Autowired
    public SynchronizeDataController(GetUsersHomeData getUsersHomeData, JwtUtil jwtUtil, SaveUsersHomeData saveUsersHomeData) {
        this.getUsersHomeData = getUsersHomeData;
        this.jwtUtil = jwtUtil;
        this.saveUsersHomeData = saveUsersHomeData;
    }

    @GetMapping("/api/get-users-data")
    public ResponseEntity<?> getUsersData(@RequestHeader() String authorization) {
        try {
            return new ResponseEntity<>(getUsersHomeData.get(getUsernameFromToken(authorization)), HttpStatus.OK);
        } catch (UserDoesNotExistException e) {
            return new ResponseEntity<>("User does not exist", HttpStatus.BAD_REQUEST);
        }
    }

    private String getUsernameFromToken(@RequestHeader String authorization) {
        String token = authorization.substring(7);
        return jwtUtil.extractUsername(token);
    }

    @RequestMapping(value = "/api/save-users-data", method = RequestMethod.POST)
    public ResponseEntity<?> saveUsersData(@RequestHeader String authorization, @RequestBody DataViewModel viewModel) {
        String usernameFromToken = getUsernameFromToken(authorization);
        try {
            saveUsersHomeData.save(viewModel, usernameFromToken);
        } catch (InvalidDataException e) {
            return new ResponseEntity<>("Invalid Data", HttpStatus.BAD_REQUEST);
        } catch (UserDoesNotExistException e) {
            return new ResponseEntity<>("User: '" + usernameFromToken + "' does not exist", HttpStatus.valueOf(403));
        }
        return new ResponseEntity<>("Saved", HttpStatus.OK);
    }


}
