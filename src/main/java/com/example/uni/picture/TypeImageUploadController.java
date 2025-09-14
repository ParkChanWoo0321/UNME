package com.example.uni.picture;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/type-images")
public class TypeImageUploadController {

    @Value("${app.upload.dir:./uploads}")
    private String uploadRoot;
    private String contextPath; // 예: "/api" 또는 ""

    @PostMapping(value = "/{type}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object upload(@PathVariable int type,
                         @RequestPart("file") MultipartFile file,
                         HttpServletRequest req) throws IOException {

        if (type < 1 || type > 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type은 1~4만 허용");
        }
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file 파트가 비어있음");
        }

        Path dir = Paths.get(uploadRoot, "profile-types");
        Files.createDirectories(dir);

        String original = file.getOriginalFilename();
        String ext = StringUtils.getFilenameExtension(original);
        if (!StringUtils.hasText(ext)) ext = "png";
        ext = ext.toLowerCase();

        String filename = "type" + type + "." + ext;
        Path target = dir.resolve(filename);
        file.transferTo(target.toFile());

        // 공개 URL 구성
        String scheme = req.getScheme(); // http/https
        String host = req.getServerName();
        int port = req.getServerPort();
        String base = scheme + "://" + host + ((port == 80 || port == 443) ? "" : (":" + port));
        String url = base + (contextPath == null ? "" : contextPath) + "/files/profile-types/" + filename;

        return java.util.Map.of(
                "type", type,
                "saved", target.toAbsolutePath().toString(),
                "url", url,
                "propertyKey", "app.type-image." + type,
                "propertyValue", url,
                "note", "application.properties에 위 propertyValue를 붙여넣어 사용"
        );
    }
}
