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

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/type-images")
public class TypeImageUploadController {

    @Value("${app.upload.dir:./uploads}")
    private String uploadRoot;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @PostMapping(value = "/{type}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object upload(@PathVariable String type,
                         @RequestPart("file") MultipartFile file,
                         HttpServletRequest req) throws IOException {

        boolean ok =
                type.matches("^[1-4]$") ||
                        type.matches("^2\\.[1-4]$") ||
                        type.matches("^3\\.[1-5]$") ||
                        type.matches("^4\\.(?:default|[1-9]|[1-4][0-9]|5[0-4])$") ||
                        type.matches("^5\\.(?:[1-9]|1[0-6])$");
        if (!ok) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type 범위 오류");

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file 비어있음");
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

        String scheme = req.getScheme();
        String host = req.getServerName();
        int port = req.getServerPort();
        String base = scheme + "://" + host + ((port == 80 || port == 443) ? "" : (":" + port));
        String url = base + (contextPath == null ? "" : contextPath) + "/files/profile-types/" + filename;

        String propertyKey;
        if (type.startsWith("2.")) {
            propertyKey = "app.type-image2." + type.substring(2);
        } else if (type.startsWith("3.")) {
            propertyKey = "app.type-image3." + type.substring(2);
        } else if (type.startsWith("4.")) {
            propertyKey = "app.type-image4." + type.substring(2);
        } else if (type.startsWith("5.")) {
            propertyKey = "app.type-image5." + type.substring(2);
        } else {
            propertyKey = "app.type-image." + type;
        }

        return java.util.Map.of(
                "type", type,
                "saved", target.toAbsolutePath().toString(),
                "url", url,
                "propertyKey", propertyKey,
                "propertyValue", url
        );
    }
}
