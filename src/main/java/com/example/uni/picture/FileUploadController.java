// com/example/uni/picture/FileUploadController.java
package com.example.uni.picture;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileUploadController {

    @Value("${app.upload.dir:./uploads}")
    private String uploadRoot;

    @Value("${app.api-prefix:/api}")
    private String apiPrefix;

    @Value("${app.public-base-url:}")
    private String publicBaseUrl;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String,Object>> upload(
            @AuthenticationPrincipal String principal,
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        // 간단 서버측 검증(프론트와 일치): 이미지 MIME, 10MB 제한
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "EMPTY_FILE"));
        }
        if (file.getSize() > 10L * 1024 * 1024) { // 10MB
            return ResponseEntity.badRequest().body(Map.of("error", "FILE_TOO_LARGE"));
        }
        String contentType = Optional.ofNullable(file.getContentType()).orElse("");
        if (!contentType.toLowerCase().startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of("error", "NOT_IMAGE"));
        }

        Long uid = principal != null ? Long.valueOf(principal) : null;
        String original = StringUtils.cleanPath(Optional.ofNullable(file.getOriginalFilename()).orElse(""));
        String ext = "";
        int idx = original.lastIndexOf('.');
        if (idx >= 0) ext = original.substring(idx);
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;

        Path dir = uid != null
                ? Paths.get(uploadRoot, "profile-images", String.valueOf(uid))
                : Paths.get(uploadRoot, "profile-images");
        Files.createDirectories(dir);
        Path target = dir.resolve(filename);
        Files.write(target, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        // context-path(/api) 및 외부 base-url 반영
        String prefix = apiPrefix == null ? "" : apiPrefix.trim();
        if (!prefix.isEmpty() && !prefix.startsWith("/")) prefix = "/" + prefix;
        String rel = (prefix + "/files/profile-images/" + (uid != null ? uid + "/" : "") + filename)
                .replaceAll("//+", "/");

        String base = publicBaseUrl == null ? "" : publicBaseUrl.trim();
        if (!base.isEmpty()) {
            base = base.replaceAll("/+$", "");
            return ResponseEntity.ok(Map.of("url", base + rel));
        } else {
            return ResponseEntity.ok(Map.of("url", rel));
        }
    }
}
