package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.ChangeUsersProfileDataUseCase;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.EmailAlreadyExistsException;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.exceptions.UserAlreadyExistsException;
import com.project.cuecards.exceptions.UsernameAlreadyExistsException;
import com.project.cuecards.gateways.UserGateway;
import com.project.cuecards.services.AuthenticateService;
import com.project.cuecards.services.ValidateUserDataInputServiceImpl;
import com.project.cuecards.viewModels.AuthenticationResponse;
import com.project.cuecards.viewModels.ChangeUserViewModel;
import com.project.cuecards.viewModels.UserViewModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeUsersProfileDataUseCaseImplTest {

    @Mock private UserGateway userGatewayMock;
    @Mock private AuthenticateService authenticateServiceMock;
    @Mock private PasswordEncoder passwordEncoderMock;
    private ChangeUsersProfileDataUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ChangeUsersProfileDataUseCaseImpl(userGatewayMock,
                new ValidateUserDataInputServiceImpl(userGatewayMock), authenticateServiceMock,
                passwordEncoderMock);
    }

    @Test
    public void givenUserIsNull_thenThrowException() throws Exception {
        Assertions.assertThrows(InvalidDataException.class, () -> useCase.change(new ChangeUserViewModel(), null));
    }

    @Test
    public void givenUser_thenFindUserFromDbAndOverwriteValuesAndSave() throws Exception {
        User user = (User) new User().setNickName("oldName").setEmail("old@email").setPictureUrl("oldUrl")
                .setUsername("oldUsername").setPassword("oldPassword").setId(123L);
        when(userGatewayMock.getUserById(123L)).thenReturn(user);
        AuthenticationResponse response = new AuthenticationResponse("token", new UserViewModel());
        when(authenticateServiceMock.authenticate(any())).thenReturn(response);
        when(passwordEncoderMock.encode("newPassword")).thenReturn("encodedPassword");
        User loggedInUser = (User) new User().setId(123L);
        ChangeUserViewModel userViewModel = new ChangeUserViewModel();
        userViewModel.username = "newUsername";
        userViewModel.password = "newPassword";
        userViewModel.nickName = "newName";
        userViewModel.email = "new@email";
        userViewModel.userImage = "newUrl";

        AuthenticationResponse authenticationResponse = useCase.change(userViewModel, loggedInUser);

        verify(userGatewayMock, times(1)).saveUser(user);
        assertEquals("newName", user.getNickName());
        assertEquals("newUrl", user.getPictureUrl());
        assertEquals("new@email", user.getEmail());
        assertEquals("newUsername", user.getUsername());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals(response, authenticationResponse);
    }

    @Test
    public void givenUserWithoutOverwrittenEmailOrUsername_thenDoNotThrowException() throws Exception {
        User user = (User) new User().setNickName("oldName").setEmail("old@email").setPictureUrl("oldUrl").setUsername("oldUsername").setId(123L);
        when(userGatewayMock.getUserById(123L)).thenReturn(user);
        User loggedInUser = (User) new User().setId(123L);
        ChangeUserViewModel userViewModel = new ChangeUserViewModel();
        userViewModel.username = "oldUsername";
        userViewModel.nickName = "newName";
        userViewModel.email = "old@email";
        userViewModel.userImage = "newUrl";
        List<User> users = new ArrayList<>();
        users.add(user);
        when(userGatewayMock.getUserByUsernameOrEmail("oldUsername", "old@email")).thenReturn(users);

        useCase.change(userViewModel, loggedInUser);

        verify(userGatewayMock, times(1)).saveUser(user);
        assertEquals("newName", user.getNickName());
        assertEquals("newUrl", user.getPictureUrl());
        assertEquals("old@email", user.getEmail());
        assertEquals("oldUsername", user.getUsername());
    }

    @Test
    public void givenUsernameAlreadyExists_thenThrowExeption() throws Exception {
        User user = (User) new User().setNickName("oldName").setEmail("old@email").setPictureUrl("oldUrl").setUsername("oldUsername").setId(123L);
        when(userGatewayMock.getUserById(123L)).thenReturn(user);
        User loggedInUser = (User) new User().setId(123L);
        ChangeUserViewModel userViewModel = new ChangeUserViewModel();
        userViewModel.username = "newUsername";
        userViewModel.nickName = "newName";
        userViewModel.email = "new@email";
        userViewModel.userImage = "newUrl";
        ArrayList<User> users = new ArrayList<>();
        users.add(new User().setUsername("newUsername").setEmail("other@email"));
        when(userGatewayMock.getUserByUsernameOrEmail(eq("newUsername"), anyString())).thenReturn(users);

        Assertions.assertThrows(UsernameAlreadyExistsException.class, () -> useCase.change(userViewModel, loggedInUser));
    }

    @Test
    public void givenEmailAlreadyExists_thenThrowException() throws Exception {
        User user = (User) new User().setNickName("oldName").setEmail("old@email").setPictureUrl("oldUrl").setUsername("oldUsername").setId(123L);
        when(userGatewayMock.getUserById(123L)).thenReturn(user);
        User loggedInUser = (User) new User().setId(123L);
        ChangeUserViewModel userViewModel = new ChangeUserViewModel();
        userViewModel.username = "newUsername";
        userViewModel.nickName = "newName";
        userViewModel.email = "new@email";
        userViewModel.userImage = "newUrl";
        ArrayList<User> users = new ArrayList<>();
        users.add(new User().setUsername("otherUsername").setEmail("new@email"));
        when(userGatewayMock.getUserByUsernameOrEmail(anyString(), eq("new@email"))).thenReturn(users);

        Assertions.assertThrows(EmailAlreadyExistsException.class, () -> useCase.change(userViewModel, loggedInUser));
    }


    @Test
    public void givenUsernameAndEmailAlreadyExists_thenThrowException() throws Exception {
        User user = (User) new User().setNickName("oldName").setEmail("old@email").setPictureUrl("oldUrl").setUsername("oldUsername").setId(123L);
        when(userGatewayMock.getUserById(123L)).thenReturn(user);
        User loggedInUser = (User) new User().setId(123L);
        ChangeUserViewModel userViewModel = new ChangeUserViewModel();
        userViewModel.username = "newUsername";
        userViewModel.nickName = "newName";
        userViewModel.email = "new@email";
        userViewModel.userImage = "newUrl";
        ArrayList<User> users = new ArrayList<>();
        users.add(new User().setUsername("newUsername").setEmail("new@email"));
        when(userGatewayMock.getUserByUsernameOrEmail(eq("newUsername"), eq("new@email"))).thenReturn(users);

        Assertions.assertThrows(UserAlreadyExistsException.class, () -> useCase.change(userViewModel, loggedInUser));
    }

    @Test
    public void givenValuesAreNull_thenDoNotOverwriteThem() throws Exception {
        User user = (User) new User().setNickName("oldName").setEmail("old@email").setPictureUrl("oldUrl")
                .setUsername("oldUsername").setPassword("oldPassword").setId(123L);
        when(userGatewayMock.getUserById(123L)).thenReturn(user);
        User loggedInUser = (User) new User().setId(123L);

        useCase.change(new ChangeUserViewModel(), loggedInUser);

        verify(userGatewayMock, times(1)).saveUser(user);
        assertEquals("oldName", user.getNickName());
        assertEquals("old@email", user.getEmail());
        assertEquals("oldUrl", user.getPictureUrl());
        assertEquals("oldUsername", user.getUsername());
        assertEquals("oldPassword", user.getPassword());
    }
}