package com.example.chatu.user;

import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
