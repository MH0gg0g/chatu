package com.example.chatu.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chatu.service.ActiveGroupService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class ActiveGroupController {

    private final ActiveGroupService activeGroupService;

    @GetMapping("/active-groups")
    public Map<String, Integer> getActiveGroups() {
        return activeGroupService.getActiveGroups();
    }

}
