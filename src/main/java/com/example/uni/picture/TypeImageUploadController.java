// com/example/uni/picture/TypeImageUploadController.java
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
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/type-images")
public class TypeImageUploadController {

    @Value("${app.upload.dir:./uploads}")
    private String uploadRoot;
    private String contextPath; // 예: "/api" 또는 ""

    @PostMapping(value = "/{type}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object upload(@PathVariable String type,
                         @RequestPart("file") MultipartFile file,
                         HttpServletRequest req) throws IOException {

        // 허용 타입: 1,2,3,4, 2.1~2.4, 3.1~3.4
        List<String> allowedTypes = List.of(
                "1", "2", "3", "4",
                "2.1", "2.2", "2.3", "2.4",
                "3.1", "3.2", "3.3", "3.4"
        );
        if (!allowedTypes.contains(type)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type은 1~4, 2.1~2.4, 3.1~3.4만 허용");
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

        String filename = "type" + type + "." + ext; // ex) type3.1.png
        Path target = dir.resolve(filename);
        file.transferTo(target.toFile());

        // 공개 URL
        String scheme = req.getScheme(); // http/https
        String host = req.getServerName();
        int port = req.getServerPort();
        String base = scheme + "://" + host + ((port == 80 || port == 443) ? "" : (":" + port));
        String url = base + (contextPath == null ? "" : contextPath) + "/files/profile-types/" + filename;

        // 프로퍼티 키 접두사 분기
        String propertyKey;
        if (type.startsWith("2.")) {
            propertyKey = "app.type-image2." + type.substring(2);
        } else if (type.startsWith("3.")) {
            propertyKey = "app.type-image3." + type.substring(2);
        } else {
            propertyKey = "app.type-image." + type;
        }

        return java.util.Map.of(
                "type", type,
                "saved", target.toAbsolutePath().toString(),
                "url", url,
                "propertyKey", propertyKey,
                "propertyValue", url,
                "note", "application.properties에 위 propertyValue를 붙여넣어 사용"
        );
    }
}