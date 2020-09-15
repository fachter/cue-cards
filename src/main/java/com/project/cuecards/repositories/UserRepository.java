package com.project.cuecards.repositories;

import com.project.cuecards.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByUsername(String username);

    List<User> findUsersByUsernameOrEmail(String username, String email);
}
