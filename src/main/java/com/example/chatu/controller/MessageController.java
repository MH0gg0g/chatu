package com.example.chatu.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import com.example.chatu.dto.GroupMessageDTO;
import com.example.chatu.dto.PrivateMessageDTO;
import com.example.chatu.service.ActiveGroupService;
import com.example.chatu.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@Validated
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ActiveGroupService activeGroupService;
    private final UserService userService;

    @MessageMapping("/private.send")
    public void sendPrivate(@Payload @Valid PrivateMessageDTO payload, Principal principal) {
        String toUsername = payload.getTo();
        String content = payload.getContent();

        if (principal == null)
            return;

        userService.findByUsername(principal.getName()).ifPresent(sender -> {
            userService.findByUsername(toUsername).ifPresent(receiver -> {
                Map<String, String> messagePayload = new HashMap<>();
                messagePayload.put("from", sender.getUsername());
                messagePayload.put("content", content);
                messagePayload.put("timestamp", LocalDateTime.now().toString());

                messagingTemplate.convertAndSendToUser(sender.getUsername(), "/queue/private", messagePayload);
                if (!sender.getUsername().equals(receiver.getUsername())) {
                    messagingTemplate.convertAndSendToUser(receiver.getUsername(), "/queue/private", messagePayload);
                }
            });
        });
    }

    @MessageMapping("/group.send")
    public void sendGroup(@Payload @Valid GroupMessageDTO payload, Principal principal) {
        String groupId = payload.getGroupId();
        String content = payload.getContent();

        if (principal == null)
            return;

        userService.findByUsername(principal.getName()).ifPresent(sender -> {
            activeGroupService.addUserToGroup(groupId, sender.getUsername());
            Map<String, String> messagePayload = Map.of(
                    "from", sender.getUsername() + " (" + groupId + ")",
                    "content", content,
                    "timestamp", LocalDateTime.now().toString());

            messagingTemplate.convertAndSend("/topic/group." + groupId, messagePayload);
        });
    }
}
