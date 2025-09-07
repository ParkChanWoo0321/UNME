package com.example.uni.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String root = Paths.get(uploadDir).toAbsolutePath().toString().replace("\\","/"); // Windows 대응
        if (!root.endsWith("/")) root = root + "/";
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + root)
                .setCachePeriod(3600);
    }
}
