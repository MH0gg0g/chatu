package com.example.chatu.webSocket;

import java.security.Principal;
import java.util.List;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import com.example.chatu.secuirty.JwtService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
                StompHeaderAccessor.class);
        log.info("WebSocketAuthChannelInterceptor - preSend called with accessor {}", accessor);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> auth = accessor.getNativeHeader("Authorization");
            log.info("Authorization Header: {}", auth);
            if (auth != null && !auth.isEmpty()) {
                String bearer = auth.get(0);
                if (bearer.startsWith("Bearer ")) {
                    String token = bearer.substring(7);
                    if (jwtService.isValid(token)) {
                        String username = jwtService.getUsernameFromToken(token);
                        log.info("WebSocket connection authenticated for user: {}", username);
                        Principal p = new UsernamePasswordAuthenticationToken(username, null);
                        accessor.setUser(p);
                        log.info("Principal set for user: {}", p);
                    }
                }
            }
        }
        return message;
    }
}
