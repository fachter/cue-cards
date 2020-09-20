package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.UsersProfileDataUseCase;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.services.UsersProfileDataServiceImpl;
import com.project.cuecards.viewModels.UserViewModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsersProfileDataUseCaseImplTest {

    private UsersProfileDataUseCase useCase;
    @BeforeEach
    void setUp() {
        useCase = new UsersProfileDataUseCaseImpl(new UsersProfileDataServiceImpl());
    }

    @Test
    public void givenUserIsNull_thenThrowException() {
        Assertions.assertThrows(InvalidDataException.class, () -> useCase.get(null));
    }

    @Test
    public void givenUser_thenReturnFilledViewModel() throws Exception {
        User user = (User) new User().setNickName("Felix").setEmail("felix@email.de").setPictureUrl("url")
                .setUsername("username").setId(3L);

        UserViewModel userViewModel = useCase.get(user);

        assertEquals("username", userViewModel.username);
        assertEquals("Felix", userViewModel.nickName);
        assertEquals("url", userViewModel.userImage);
        assertEquals("felix@email.de", userViewModel.email);
    }

    @Test
    public void givenImageUrlIsNull_thenReturnDefault() throws Exception {
        User user = (User) new User().setNickName("Felix").setEmail("felix@email.de").setPictureUrl(null)
                .setUsername("username").setId(3L);

        UserViewModel userViewModel = useCase.get(user);

        assertEquals("username", userViewModel.username);
        assertEquals("Felix", userViewModel.nickName);
        assertEquals("https://res.cloudinary.com/dilnshj2a/image/upload/v1600454278/ProfilePictures/xsfgjilvywtwnazbsz8g.jpg", userViewModel.userImage);
        assertEquals("felix@email.de", userViewModel.email);
    }
}