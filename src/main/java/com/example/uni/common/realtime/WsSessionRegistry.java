package com.example.uni.common.realtime;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WsSessionRegistry {
    private final ConcurrentHashMap<String, Set<String>> map = new ConcurrentHashMap<>();

    public void add(String userId, String sessionId) {
        map.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
    }

    public void remove(String userId, String sessionId) {
        Set<String> set = map.get(userId);
        if (set != null) {
            set.remove(sessionId);
            if (set.isEmpty()) map.remove(userId);
        }
    }

    public Set<String> sessions(String userId) {
        return map.getOrDefault(userId, Collections.emptySet());
    }
}