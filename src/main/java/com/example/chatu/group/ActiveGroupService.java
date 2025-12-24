package com.example.chatu.group;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class ActiveGroupService {

    private final Map<String, Set<String>> groups = new ConcurrentHashMap<>();

    public void addUserToGroup(String groupId, String username) {
        groups.computeIfAbsent(groupId, k -> ConcurrentHashMap.newKeySet()).add(username);
    }

    public void removeUserFromGroup(String username) {
        groups.forEach((groupId, members) -> {
            members.remove(username);
            if (members.isEmpty()) {
                groups.remove(groupId);
            }
        });
    }

    public Map<String, Integer> getActiveGroups() {
        Map<String, Integer> result = new ConcurrentHashMap<>();
        groups.forEach((gid, members) -> result.put(gid, members.size()));
        return result;
    }
}