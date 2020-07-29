package com.project.cuecards.gateways;

import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.UserDoesNotExistException;
import com.project.cuecards.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserGatewayImplTest {

    @Mock private UserRepository userRepositoryMock;
    private UserGateway userGateway;

    @BeforeEach
    void setUp() {
        userGateway = new UserGatewayImpl(userRepositoryMock);
    }

    @Test
    public void givenUsernameDoesNotExist_throwException() throws Exception {
        when(userRepositoryMock.findUserByUsername("invalidUsername"))
                .thenReturn(null);

        assertThrows(UserDoesNotExistException.class,
                () -> userGateway.getUserByUsername("invalidUsername"));
    }

    @Test
    public void givenUsernameExists_thenReturnUser() throws Exception {
        User expectedUser = new User();
        expectedUser.setUsername("validUsername");
        expectedUser.setPassword("somePassword");
        expectedUser.setId(1L);
        when(userRepositoryMock.findUserByUsername("validUsername"))
                .thenReturn(expectedUser);

        User actualUser = userGateway.getUserByUsername("validUsername");

        assertEquals(expectedUser, actualUser);
    }
}
