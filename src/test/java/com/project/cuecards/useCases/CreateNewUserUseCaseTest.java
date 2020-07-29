package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.CreateNewUser;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.exceptions.UserAlreadyExistsException;
import com.project.cuecards.gateways.UserGateway;
import com.project.cuecards.viewModels.RegisterRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateNewUserUseCaseTest {

    private CreateNewUser createNewUser;
    @Mock private UserGateway userGatewayMock;

    @BeforeEach
    void setUp() {
        createNewUser = new CreateNewUserUseCase(userGatewayMock);
    }

    @Test
    public void givenNoRegisterRequest_throwInvalidDataException() {
        Assertions.assertThrows(InvalidDataException.class, () -> createNewUser.create(null));
    }

    @ParameterizedTest
    @CsvSource(value = {",ok,ok", "ok,,ok", "ok,ok,"})
    public void givenEmptyStringAsValue_throwInvalidDataException(String name, String pw, String mail) {
        RegisterRequest registerRequest = new RegisterRequest()
                .setUsername(Objects.toString(name, ""))
                .setPassword(Objects.toString(pw, ""))
                .setEmail(Objects.toString(mail, ""));

        Assertions.assertThrows(InvalidDataException.class, () -> createNewUser.create(registerRequest));
    }

    @ParameterizedTest
    @CsvSource(value = {",ok,ok", "ok,,ok", "ok,ok,"})
    public void givenNullAsValue_throwInvalidDataException(String name, String pw, String mail) {
        RegisterRequest registerRequest = new RegisterRequest()
                .setUsername(name)
                .setPassword(pw)
                .setEmail(mail);

        Assertions.assertThrows(InvalidDataException.class, () -> createNewUser.create(registerRequest));
    }

    @Test
    public void givenUsernameAlreadyExists_throwException() throws Exception {
        doThrow(new UserAlreadyExistsException()).when(userGatewayMock).addUser(any(User.class));
        RegisterRequest registerRequest = new RegisterRequest()
                .setUsername("existingUser")
                .setPassword("test")
                .setEmail("mail");
        Assertions.assertThrows(UserAlreadyExistsException.class, () -> createNewUser.create(registerRequest));
    }

    @Test
    public void givenAllFieldsAreSet() throws Exception {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        RegisterRequest registerRequest = new RegisterRequest()
                .setUsername("newUser")
                .setPassword("test")
                .setEmail("email")
                .setFullName("New User");
        User expectedUser = new User();
        expectedUser.setUsername("newUser");
        expectedUser.setPassword("test");
        expectedUser.setEmail("email");
        expectedUser.setFullName("New User");

        createNewUser.create(registerRequest);

        verify(userGatewayMock, times(1)).addUser(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(expectedUser);
    }

    @Test
    public void givenNoFullName_thenUseUsername() throws Exception {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        RegisterRequest registerRequest = new RegisterRequest()
                .setUsername("newUser")
                .setPassword("test")
                .setEmail("email");
        User expectedUser = new User();
        expectedUser.setUsername("newUser");
        expectedUser.setPassword("test");
        expectedUser.setEmail("email");
        expectedUser.setFullName("newUser");

        createNewUser.create(registerRequest);

        verify(userGatewayMock, times(1)).addUser(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(expectedUser);
    }
}