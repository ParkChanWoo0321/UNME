package com.example.uni.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class HttpClientConfig {

    @Bean
    HttpComponentsClientHttpRequestFactory kakaoRequestFactory(
            @Value("${client.http.connect-timeout-ms:3000}") int connectTimeoutMs,
            @Value("${client.http.read-timeout-ms:7000}") int readTimeoutMs
    ) {
        HttpComponentsClientHttpRequestFactory f = new HttpComponentsClientHttpRequestFactory();
        f.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        f.setConnectionRequestTimeout(Duration.ofMillis(connectTimeoutMs));
        f.setReadTimeout(Duration.ofMillis(readTimeoutMs));
        return f;
    }

    @Bean(name = "kakaoRestClient")
    RestClient kakaoRestClient(
            @Value("${kakao.api.base-url}") String baseUrl,
            HttpComponentsClientHttpRequestFactory factory
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }
}