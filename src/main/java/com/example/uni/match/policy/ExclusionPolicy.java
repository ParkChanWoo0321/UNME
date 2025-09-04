package com.example.uni.match.policy;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ExclusionPolicy {
    public static Set<UUID> buildDefaults(UUID me){
        Set<UUID> s = new HashSet<>();
        s.add(me); // 자기자신 제외
        return s;
    }
}
