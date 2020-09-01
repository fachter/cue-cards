package com.project.cuecards.gateways;

import com.project.cuecards.entities.Role;
import com.project.cuecards.exceptions.RoleDoesNotExistException;
import com.project.cuecards.repositories.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleGatewayImpl implements RoleGateway {

    private final RoleRepository roleRepository;

    public RoleGatewayImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role getUserRole() throws RoleDoesNotExistException {
        Role role = roleRepository.findByName("USER");
        if (role == null)
            throw new RoleDoesNotExistException();
        return role;
    }
}
