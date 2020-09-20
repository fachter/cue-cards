package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.CreateNewUser;
import com.project.cuecards.entities.Role;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.*;
import com.project.cuecards.gateways.RoleGateway;
import com.project.cuecards.gateways.UserGateway;
import com.project.cuecards.services.ValidateUserDataInputServiceImpl;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateNewUserUseCaseTest {

    private CreateNewUser createNewUser;
    @Mock private UserGateway userGatewayMock;
    @Mock private PasswordEncoder passwordEncoderMock;
    @Mock private RoleGateway roleGatewayMock;
    private final String encodedPassword = "%password!123";

    @BeforeEach
    void setUp() {
        createNewUser = new CreateNewUserUseCase(new ValidateUserDataInputServiceImpl(userGatewayMock),
                userGatewayMock, passwordEncoderMock, roleGatewayMock);
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
    public void givenUsernameAlreadyExists_thenThrowException() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest()
                .setUsername("existingUsername")
                .setPassword("test")
                .setEmail("mail");
        List<User> users = new ArrayList<>();
        users.add(new User().setUsername("existingUsername").setPassword("pw").setEmail("newMail"));
        when(userGatewayMock.getUserByUsernameOrEmail(eq("existingUsername"), anyString())).thenReturn(users);
        Assertions.assertThrows(UsernameAlreadyExistsException.class, () -> createNewUser.create(registerRequest));
    }

    @Test
    public void givenEmailAlreadyExists_thenThrowException() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest()
                .setUsername("existingUser")
                .setPassword("test")
                .setEmail("mail");
        List<User> users = new ArrayList<>();
        users.add(new User().setUsername("new").setPassword("pw").setEmail("mail"));
        when(userGatewayMock.getUserByUsernameOrEmail(anyString(), eq("mail"))).thenReturn(users);
        Assertions.assertThrows(EmailAlreadyExistsException.class, () -> createNewUser.create(registerRequest));
    }

    @Test
    public void givenUsernameAndEmailAlreadyExists_thenThrowException() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest()
                .setUsername("existingUsername")
                .setPassword("test")
                .setEmail("mail");
        List<User> users = new ArrayList<>();
        users.add(new User().setUsername("existingUsername").setPassword("pw").setEmail("newMail"));
        users.add(new User().setUsername("newUsername").setPassword("pw").setEmail("mail"));
        when(userGatewayMock.getUserByUsernameOrEmail(eq("existingUsername"), eq("mail"))).thenReturn(users);
        Assertions.assertThrows(UserAlreadyExistsException.class, () -> createNewUser.create(registerRequest));
    }

    @Test
    public void givenAllFieldsAreSet() throws Exception {
        when(passwordEncoderMock.encode("test")).thenReturn(encodedPassword);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        RegisterRequest registerRequest = new RegisterRequest()
                .setUsername("newUser")
                .setPassword("test")
                .setEmail("email")
                .setFullName("New User");
        User expectedUser = new User();
        expectedUser.setUsername("newUser");
        expectedUser.setPassword(encodedPassword);
        expectedUser.setEmail("email");
        expectedUser.setNickName("New User");
        Role role = new Role();
        role.setName("USER");
        role.setDescription("User Role");
        expectedUser.getRoles().add(role);
        when(roleGatewayMock.getUserRole()).thenThrow(RoleDoesNotExistException.class);

        createNewUser.create(registerRequest);

        verify(userGatewayMock, times(1)).addUser(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(expectedUser);
    }

    @Test
    public void givenNoFullName_thenUseUsername() throws Exception {
        when(passwordEncoderMock.encode("test")).thenReturn(encodedPassword);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        RegisterRequest registerRequest = new RegisterRequest()
                .setUsername("newUser")
                .setPassword("test")
                .setEmail("email");
        User expectedUser = new User();
        expectedUser.setUsername("newUser");
        expectedUser.setPassword(encodedPassword);
        expectedUser.setEmail("email");
        expectedUser.setNickName("newUser");
        Role role = new Role();
        role.setName("USER");
        role.setDescription("User Role");
        expectedUser.getRoles().add(role);
        when(roleGatewayMock.getUserRole()).thenThrow(RoleDoesNotExistException.class);

        createNewUser.create(registerRequest);

        verify(userGatewayMock, times(1)).addUser(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(expectedUser);
    }

    @Test
    public void givenUserRoleExistsAlready_thenUseSameRole() throws Exception {
        Role role = new Role();
        role.setId(123L);
        role.setName("USER");
        role.setDescription("User Role");
        when(roleGatewayMock.getUserRole()).thenReturn(role);
        when(passwordEncoderMock.encode("test")).thenReturn(encodedPassword);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        RegisterRequest registerRequest = new RegisterRequest()
                .setUsername("newUser")
                .setPassword("test")
                .setEmail("email");

        createNewUser.create(registerRequest);

        verify(userGatewayMock, times(1)).addUser(captor.capture());
        User user = captor.getValue();
        assertTrue(user.getRoles().contains(role));
    }
}