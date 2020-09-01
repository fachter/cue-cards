package com.project.cuecards.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    @RequestMapping("/")
    public String getHome() {
        return "index";
    }

    @RequestMapping("/freitext")
    public String getFreitext() {
        return "freitext";
    }

    @RequestMapping("/multiplechoice")
    public String getMultipleChoice() {
        return "multiplechoice";
    }

    @RequestMapping("/vokabel")
    public String getVokabel() {
        return "vokabel";
    }


}
