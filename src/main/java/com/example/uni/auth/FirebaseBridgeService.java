// com/example/uni/auth/FirebaseBridgeService.java
package com.example.uni.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.stereotype.Service;

@Service
public class FirebaseBridgeService {

    /** 내 앱의 사용자 ID(문자열)로 Firebase 커스텀 토큰 생성 */
    public String createCustomToken(String uid) {
        try {
            return FirebaseAuth.getInstance().createCustomToken(uid);
        } catch (FirebaseAuthException e) {
            throw new IllegalStateException("Failed to create Firebase custom token", e);
        }
    }
}
