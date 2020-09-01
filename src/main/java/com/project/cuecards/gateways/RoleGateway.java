package com.project.cuecards.gateways;

import com.project.cuecards.entities.Role;
import com.project.cuecards.exceptions.RoleDoesNotExistException;

public interface RoleGateway {
    Role getUserRole() throws RoleDoesNotExistException;
}
