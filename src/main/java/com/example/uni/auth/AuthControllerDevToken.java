package com.example.uni.auth;

import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthControllerDevToken {

    private final UserRepository userRepository;
    private final OAuthService oAuthService;

    /** 개발용: userId로 즉시 Access 토큰 발급 */
    @PostMapping("/dev/token")
    public ResponseEntity<?> devIssue(@RequestParam("userId") UUID userId) {
        // 유효한 유저인지 확인 (없으면 404)
        userRepository.findById(userId).orElseThrow();

        String access = oAuthService.issueAccessToken(userId.toString());
        long expiresIn = oAuthService.getAccessTtlSeconds();

        return ResponseEntity.ok(Map.of(
                "accessToken", access,
                "expiresIn", expiresIn
        ));
    }
}
