package com.example.chatu.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.chatu.service.UserService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class WebController {
    private final UserService userService;

    @GetMapping({ "/", "/index" })
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/chat")
    public String chat(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            userService.findByUsername(userDetails.getUsername()).ifPresent(u -> model.addAttribute("me", u));
        }
        return "chat";
    }
}
