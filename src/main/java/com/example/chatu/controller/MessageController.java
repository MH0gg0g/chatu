package com.example.chatu.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.chatu.entity.User;
import com.example.chatu.service.ActiveGroupService;
import com.example.chatu.service.UserService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ActiveGroupService activeGroupService;
    private final UserService userService;

    @MessageMapping("/private.send")
    public void sendPrivate(@Payload Map<String, String> payload, Principal principal) {
        String toUsername = payload.get("to");
        String content = payload.get("content");
        var senderOpt = userService.findByUsername(principal.getName());
        var receiverOpt = userService.findByUsername(toUsername);

        if (senderOpt.isPresent() && receiverOpt.isPresent()) {
            User sender = senderOpt.get();
            User receiver = receiverOpt.get();

            Map<String, String> messagePayload = Map.of(
                    "from", sender.getUsername(),
                    "content", content,
                    "timestamp", LocalDateTime.now().toString());

            messagingTemplate.convertAndSendToUser(sender.getUsername(), "/queue/private", messagePayload);
            messagingTemplate.convertAndSendToUser(receiver.getUsername(), "/queue/private", messagePayload);

        }
    }

    @MessageMapping("/group.send")
    public void sendGroup(@Payload Map<String, String> payload, Principal principal) {
        String groupId = payload.get("groupId");
        String content = payload.get("content");
        var senderOpt = userService.findByUsername(principal.getName());

        if (senderOpt.isPresent()) {
            User sender = senderOpt.get();

            activeGroupService.addUserToGroup(groupId, sender.getUsername());
            messagingTemplate.convertAndSend("/topic/group." + groupId, Map.of(
                    "from", sender.getUsername(),
                    "content", content,
                    "timestamp", LocalDateTime.now().toString()));
        }
    }
}
