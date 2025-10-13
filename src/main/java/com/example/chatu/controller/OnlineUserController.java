package com.example.chatu.controller;

import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chatu.service.OnlineUserService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class OnlineUserController {

    private OnlineUserService onlineUserService;

    @GetMapping("/online-users")
    public Set<String> getOnlineUsers() {
        return onlineUserService.getOnlineUsers();
    }
}
