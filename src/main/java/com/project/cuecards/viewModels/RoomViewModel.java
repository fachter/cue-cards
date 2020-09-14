package com.project.cuecards.viewModels;

import java.util.ArrayList;
import java.util.List;

public class RoomViewModel {

    public Long id;
    public String name;
    public String password;
    public int pictureNumber;
    public DataViewModel data = new DataViewModel();
    public List<UserViewModel> users = new ArrayList<>();
}
