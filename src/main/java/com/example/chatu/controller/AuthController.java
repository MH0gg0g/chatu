package com.example.chatu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.chatu.service.UserService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
	private final UserService userService;

	@PostMapping("/register")
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

}
