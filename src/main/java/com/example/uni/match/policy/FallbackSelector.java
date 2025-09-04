package com.example.uni.match.policy;

import com.example.uni.user.domain.User;

import java.util.*;
import java.util.stream.Collectors;

public class FallbackSelector {
    public static List<User> pick(List<User> pool, Set<UUID> exclude, int needed){
        List<User> list = pool.stream().filter(u -> !exclude.contains(u.getId())).collect(Collectors.toList());
        Collections.shuffle(list);
        return list.stream().limit(needed).toList();
    }
}
