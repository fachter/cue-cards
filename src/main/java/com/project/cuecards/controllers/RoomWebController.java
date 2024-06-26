package com.project.cuecards.controllers;

import com.project.cuecards.boundaries.GetRoomsWebUseCase;
import com.project.cuecards.exceptions.InvalidDataException;
import com.project.cuecards.services.LoggedInUserService;
import com.project.cuecards.viewModels.RoomViewModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(path = "room")
public class RoomWebController {

    private final GetRoomsWebUseCase getRoomsWebUseCase;

    public RoomWebController(GetRoomsWebUseCase getRoomsWebUseCase) {
        this.getRoomsWebUseCase = getRoomsWebUseCase;
    }

    @GetMapping("/list")
    public String getListRooms(Model model) {
        List<RoomViewModel> roomList;
        try {
            roomList = getRoomsWebUseCase.get(LoggedInUserService.getLoggedInUser());
        } catch (InvalidDataException e) {
            roomList = new ArrayList<>();
        }
        model.addAttribute("rooms", roomList);
        return "listRooms";
    }

    @PostMapping("/show")
    public String showRoom(RoomViewModel room) {
        return "listRooms";
    }
}
