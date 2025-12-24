package com.example.chatu.webSocket;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.chatu.group.ActiveGroupService;
import com.example.chatu.user.OnlineUserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class WebSocketTracker {

    private final OnlineUserService onlineUserService;
    private final ActiveGroupService activeGroupService;

    // stomp session esbalished over websockets
    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("Session connected: {}", headerAccessor.getUser());
        String username = headerAccessor.getUser().getName();
        if (username != null) {
            onlineUserService.addUser(username);
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("Session disconnected: {}", headerAccessor.getUser());
        String username = headerAccessor.getUser().getName();
        if (username != null) {
            onlineUserService.removeUser(username);
            activeGroupService.removeUserFromGroup(username);
        }
    }
}
