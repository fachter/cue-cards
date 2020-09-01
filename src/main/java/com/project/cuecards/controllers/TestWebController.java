package com.project.cuecards.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestWebController {

    @RequestMapping("/")
    public String getHome() {
        return "home";
    }
}
