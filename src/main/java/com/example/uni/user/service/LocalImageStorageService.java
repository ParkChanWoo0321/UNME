package com.example.uni.user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Component
public class LocalImageStorageService {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Value("${app.upload.base-url:http://localhost:8080/files}")
    private String baseUrl;

    private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/jpg", "image/png");
    private static final long MAX_BYTES = 5L * 1024 * 1024; // 5MB

    public String storeProfileImage(UUID userId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IOException("empty file");
        if (file.getSize() > MAX_BYTES) throw new IOException("file too large");

        String ct = file.getContentType() == null ? "" : file.getContentType().toLowerCase();
        if (!ALLOWED.contains(ct)) throw new IOException("unsupported content-type");

        try (InputStream in = file.getInputStream()) {
            BufferedImage img = ImageIO.read(in);
            if (img == null) throw new IOException("not an image");
        }

        String ext = ct.contains("png") ? "png" : "jpg";
        String filename = userId + "_" + System.currentTimeMillis() + "." + ext;

        Path dir = Paths.get(uploadDir, "profiles");
        Files.createDirectories(dir);
        Path path = dir.resolve(filename);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        }

        String base = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        return base + "/profiles/" + filename;
    }

    public void deleteByUrl(String url) {
        if (url == null || url.isBlank()) return;
        int i = url.indexOf("/profiles/");
        if (i < 0) return;
        String rel = url.substring(i + 1); // "profiles/xxx.ext"
        Path path = Paths.get(uploadDir).resolve(rel);
        try { Files.deleteIfExists(path); } catch (IOException ignore) {}
    }
}
