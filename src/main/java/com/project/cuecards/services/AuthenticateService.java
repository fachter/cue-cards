package com.project.cuecards.services;

import com.project.cuecards.viewModels.AuthenticationRequest;
import com.project.cuecards.viewModels.AuthenticationResponse;

public interface AuthenticateService {
     AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);
}
