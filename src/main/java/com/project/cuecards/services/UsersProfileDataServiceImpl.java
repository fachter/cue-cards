package com.project.cuecards.services;

import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.viewModels.UserViewModel;
import org.springframework.stereotype.Service;

@Service
public class UsersProfileDataServiceImpl implements UsersProfileDataService {
    @Override
    public UserViewModel get(User loggedInUser) throws InvalidDataException {
        if (loggedInUser == null)
            throw new InvalidDataException();
        return getUserViewModelFromUser(loggedInUser);
    }

    @Override
    public UserViewModel getUserViewModelFromUser(User user) {
        UserViewModel userViewModel = new UserViewModel();
        userViewModel.id = user.getId();
        userViewModel.username = user.getUsername();
        userViewModel.nickName = user.getNickName();
        userViewModel.userImage = getPictureUrl(user.getPictureUrl());
        userViewModel.email = user.getEmail();
        return userViewModel;
    }

    private String getPictureUrl(String pictureUrl) {
        if (pictureUrl == null || pictureUrl.trim().length() == 0)
            return "https://res.cloudinary.com/dilnshj2a/image/upload/v1600454278/ProfilePictures/xsfgjilvywtwnazbsz8g.jpg";
        return pictureUrl;
    }
}
