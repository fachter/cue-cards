package com.project.cuecards.boundaries;

import com.project.cuecards.viewModels.AuthenticationRequest;
import com.project.cuecards.viewModels.AuthenticationResponse;

public interface Authenticate {

    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);
}
