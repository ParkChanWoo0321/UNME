package com.example.uni;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;

@SpringBootApplication(
        exclude = {
                UserDetailsServiceAutoConfiguration.class,
                ReactiveUserDetailsServiceAutoConfiguration.class // webflux starter도 쓰면 함께 제외
        }
)
public class UniApplication {
    public static void main(String[] args) {
        SpringApplication.run(UniApplication.class, args);
    }
}