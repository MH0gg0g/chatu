package com.example.chatu.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.chatu.service.UserService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class WebController {
    private final UserService userService;

    @GetMapping({ "/", "/index", "/login" })
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/api/auth/register")
    public String registerForm(@RequestParam String username, @RequestParam String email,
            @RequestParam String password,
            Model model) {

        if (userService.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }
        if (userService.findByemail(email).isPresent()) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }

        userService.register(username, email, password);
        return "redirect:/login";
    }

    @GetMapping("/chat")
    public String chat(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            userService.findByUsername(userDetails.getUsername()).ifPresent(u -> model.addAttribute("me", u));
        }
        return "chat";
    }
}
