package com.eventra.exhibitor.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/back-end/exhibitor")
public class ExhibitorBackendController {

    @GetMapping("back_end_homepage")
    public String enterBackendExhibitor(){
        return "back-end/back_end_homepage";
    }
}
