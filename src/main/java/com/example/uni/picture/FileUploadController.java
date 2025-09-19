// com/example/uni/picture/FileUploadController.java
package com.example.uni.picture;

import jakarta.servlet.http.HttpServletRequest;
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

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String,Object>> upload(
            @AuthenticationPrincipal String principal,
            @RequestPart("file") MultipartFile file,
            HttpServletRequest req
    ) throws IOException {
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

        String scheme = Optional.ofNullable(req.getHeader("X-Forwarded-Proto")).filter(s -> !s.isBlank()).orElse(req.getScheme());
        String host = Optional.ofNullable(req.getHeader("X-Forwarded-Host")).filter(s -> !s.isBlank()).orElse(req.getServerName());
        int port = req.getServerPort();
        String portPart = (host.contains(":") || port == 80 || port == 443) ? "" : ":" + port;

        String path = "/files/profile-images/" + (uid != null ? uid + "/" : "") + filename;
        String url = scheme + "://" + host + portPart + path;

        return ResponseEntity.ok(Map.of("url", url));
    }
}
