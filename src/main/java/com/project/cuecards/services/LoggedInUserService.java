package com.project.cuecards.services;

import com.project.cuecards.entities.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class LoggedInUserService {

    public static User getLoggedInUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
