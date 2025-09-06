package com.example.uni.common.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty; // ⬅ 추가
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.util.Base64;

@Configuration
@ConditionalOnProperty(prefix = "feature.firebase", name = "enabled", havingValue = "true") // ⬅ 스위치
public class FirebaseConfig {

    @Value("${firebase.project-id:}")            // ⬅ 기본값
    private String projectId;

    @Value("${firebase.service-account-path:}")  // ⬅ 기본값
    private String saPath;

    @Value("${firebase.service-account-base64:}")// ⬅ 기본값
    private String saBase64;

    @PostConstruct
    public void init() throws Exception {
        if (!FirebaseApp.getApps().isEmpty()) return;

        if ((projectId == null || projectId.isBlank()) ||
                ((saPath == null || saPath.isBlank()) && (saBase64 == null || saBase64.isBlank()))) {
            // 값이 비어있으면 그냥 스킵
            return;
        }

        GoogleCredentials cred = (saBase64 != null && !saBase64.isBlank())
                ? GoogleCredentials.fromStream(new ByteArrayInputStream(Base64.getDecoder().decode(saBase64)))
                : GoogleCredentials.fromStream(new FileInputStream(saPath));

        FirebaseOptions opts = FirebaseOptions.builder()
                .setCredentials(cred)
                .setProjectId(projectId)
                .build();
        FirebaseApp.initializeApp(opts);
    }
}
