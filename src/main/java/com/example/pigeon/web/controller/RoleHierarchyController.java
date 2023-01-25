package com.example.pigeon.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RoleHierarchyController {

    @GetMapping("/roleHierarchy")
    public ModelAndView roleHierarchy() {

        ModelAndView model = new ModelAndView();
        model.addObject("adminMessage", "Admin content available");
        model.addObject("userMessage", "User content available");
        model.setViewName("roleHierarchy");

        return model;
    }
}
