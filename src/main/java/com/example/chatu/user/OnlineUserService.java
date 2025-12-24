package com.example.chatu.user;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class OnlineUserService {
    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

    public void addUser(String username) {
        onlineUsers.add(username);
    }

    public void removeUser(String username) {
        onlineUsers.remove(username);
    }

    public Set<String> getOnlineUsers() {
        return onlineUsers;
    }
}
