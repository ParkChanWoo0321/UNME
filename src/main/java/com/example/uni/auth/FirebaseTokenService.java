package com.example.uni.auth;

import com.google.firebase.auth.FirebaseAuth;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty; // ⬅ 추가
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "feature.firebase", name = "enabled", havingValue = "true")
public class FirebaseTokenService {
    public String createCustomToken(String uid) throws Exception {
        return FirebaseAuth.getInstance().createCustomToken(uid);
    }
}
