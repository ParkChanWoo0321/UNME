package com.example.uni.common.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "feature.firebase", name = "enabled", havingValue = "true")
public class FirebaseConfig {

    @Value("${firebase.project-id:}")
    private String projectId;

    @Value("${firebase.service-account-path:}")
    private String saPath;

    @Value("${firebase.service-account-base64:}")
    private String saBase64;

    @PostConstruct
    public void init() {
        try {
            if (!FirebaseApp.getApps().isEmpty()) return;

            final boolean hasProject = StringUtils.hasText(projectId);
            final boolean hasBase64  = StringUtils.hasText(saBase64);
            final boolean hasPath    = StringUtils.hasText(saPath);

            if (!hasProject || (!hasBase64 && !hasPath)) {
                log.info("[Firebase] skipped (projectId/credentials not provided)");
                return;
            }

            try (InputStream in = hasBase64
                    ? new ByteArrayInputStream(Base64.getDecoder().decode(saBase64))
                    : Files.newInputStream(Path.of(saPath))) {

                FirebaseOptions opts = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(in))
                        .setProjectId(projectId)
                        .build();
                FirebaseApp.initializeApp(opts);
                log.info("[Firebase] initialized (projectId={})", projectId);
            }
        } catch (Exception e) {
            log.warn("[Firebase] initialization failed: {}", e.getMessage());
        }
    }
}