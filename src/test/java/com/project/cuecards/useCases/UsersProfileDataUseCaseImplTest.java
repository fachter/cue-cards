package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.UsersProfileDataUseCase;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.viewModels.UserViewModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsersProfileDataUseCaseImplTest {

    private UsersProfileDataUseCase useCase;
    @BeforeEach
    void setUp() {
        useCase = new UsersProfileDataUseCaseImpl();
    }

    @Test
    public void givenUserIsNull_thenThrowException() {
        Assertions.assertThrows(InvalidDataException.class, () -> useCase.get(null));
    }

    @Test
    public void givenUser_thenReturnFilledViewModel() throws Exception {
        User user = (User) new User().setFullName("Felix").setEmail("felix@email.de").setPictureUrl("url")
                .setId(3L);

        UserViewModel userViewModel = useCase.get(user);

        assertEquals("Felix", userViewModel.fullName);
        assertEquals("url", userViewModel.pictureUrl);
        assertEquals("felix@email.de", userViewModel.email);
    }
}