package com.example.chatu.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.chatu.service.ActiveGroupService;
import com.example.chatu.service.OnlineUserService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class WebSocketTracker {

    private OnlineUserService onlineUserService;
    private final ActiveGroupService activeGroupService;

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = headerAccessor.getUser().getName();
        if (username != null) {
            onlineUserService.addUser(username);
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = headerAccessor.getUser().getName();
        if (username != null) {
            onlineUserService.removeUser(username);
            activeGroupService.removeUserFromGroup(username);
        }
    }
}
