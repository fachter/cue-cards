package com.project.cuecards.controllers;

import com.project.cuecards.boundaries.Authenticate;
import com.project.cuecards.boundaries.CreateNewUser;
import com.project.cuecards.exceptions.EmailAlreadyExistsException;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.exceptions.UserAlreadyExistsException;
import com.project.cuecards.exceptions.UsernameAlreadyExistsException;
import com.project.cuecards.viewModels.AuthenticationRequest;
import com.project.cuecards.viewModels.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(path = "api")
public class AuthenticationController {

    private final CreateNewUser createNewUser;
    private final Authenticate authenticate;

    @Autowired
    public AuthenticationController(CreateNewUser createNewUser, Authenticate authenticate) {
        this.createNewUser = createNewUser;
        this.authenticate = authenticate;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> createNewUser(@RequestBody RegisterRequest registerRequest) {
        try {
            createNewUser.create(registerRequest);
            return new ResponseEntity<>(authenticate.authenticate(new AuthenticationRequest(
                    registerRequest.getUsername(), registerRequest.getPassword())), HttpStatus.OK);
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

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            return ResponseEntity.ok(authenticate.authenticate(authenticationRequest));
        } catch (Exception e) {
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }
    }
}
