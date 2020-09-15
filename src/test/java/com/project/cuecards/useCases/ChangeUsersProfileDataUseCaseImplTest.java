package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.ChangeUsersProfileDataUseCase;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.exceptions.UserAlreadyExistsException;
import com.project.cuecards.gateways.UserGateway;
import com.project.cuecards.viewModels.UserViewModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeUsersProfileDataUseCaseImplTest {

    @Mock private UserGateway userGatewayMock;
    private ChangeUsersProfileDataUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ChangeUsersProfileDataUseCaseImpl(userGatewayMock);
    }

    @Test
    public void givenUserIsNull_thenThrowException() throws Exception {
        Assertions.assertThrows(InvalidDataException.class, () -> useCase.change(new UserViewModel(), null));
    }

    @Test
    public void givenUser_thenFindUserFromDbAndOverwriteValuesAndSave() throws Exception {
        User user = (User) new User().setFullName("oldName").setEmail("old@email").setPictureUrl("oldUrl").setUsername("oldUsername").setId(123L);
        when(userGatewayMock.getUserById(123L)).thenReturn(user);
        User loggedInUser = (User) new User().setId(123L);
        UserViewModel userViewModel = new UserViewModel();
        userViewModel.username = "new^Username";
        userViewModel.fullName = "newName";
        userViewModel.email = "new@email";
        userViewModel.pictureUrl = "newUrl";

        useCase.change(userViewModel, loggedInUser);

        verify(userGatewayMock, times(1)).saveUser(user);
        assertEquals("newName", user.getFullName());
        assertEquals("newUrl", user.getPictureUrl());
        assertEquals("new@email", user.getEmail());
        assertEquals("oldUsername", user.getUsername());
    }

    @Test
    public void givenUserWithoutOverwrittenEmailOrUsername_thenDoNotThrowException() throws Exception {
        User user = (User) new User().setFullName("oldName").setEmail("old@email").setPictureUrl("oldUrl").setUsername("oldUsername").setId(123L);
        when(userGatewayMock.getUserById(123L)).thenReturn(user);
        User loggedInUser = (User) new User().setId(123L);
        UserViewModel userViewModel = new UserViewModel();
        userViewModel.username = "oldUsername";
        userViewModel.fullName = "newName";
        userViewModel.email = "old@email";
        userViewModel.pictureUrl = "newUrl";
        List<User> users = new ArrayList<>();
        users.add(user);
        when(userGatewayMock.getUserByUsernameOrEmail("oldUsername", "old@email")).thenReturn(users);

        useCase.change(userViewModel, loggedInUser);

        verify(userGatewayMock, times(1)).saveUser(user);
        assertEquals("newName", user.getFullName());
        assertEquals("newUrl", user.getPictureUrl());
        assertEquals("old@email", user.getEmail());
        assertEquals("oldUsername", user.getUsername());
    }

    @Test
    public void givenUsernameAlreadyExists_thenThrowExeption() throws Exception {
        User user = (User) new User().setFullName("oldName").setEmail("old@email").setPictureUrl("oldUrl").setUsername("oldUsername").setId(123L);
        when(userGatewayMock.getUserById(123L)).thenReturn(user);
        User loggedInUser = (User) new User().setId(123L);
        UserViewModel userViewModel = new UserViewModel();
        userViewModel.username = "newUsername";
        userViewModel.fullName = "newName";
        userViewModel.email = "new@email";
        userViewModel.pictureUrl = "newUrl";
        ArrayList<User> users = new ArrayList<>();
        users.add(new User().setUsername("newUsername"));
        when(userGatewayMock.getUserByUsernameOrEmail("newUsername", "new@email")).thenReturn(users);

        Assertions.assertThrows(UserAlreadyExistsException.class, () -> useCase.change(userViewModel, loggedInUser));
    }
}