package com.example.uni.common.config;

import com.example.uni.auth.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Value("${cors.allowed-origins}")
    private String allowedOrigins; // 콤마 구분 목록

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .logout(logout -> logout.disable())
                .authorizeHttpRequests(auth -> auth
                        // 프리플라이트 전부 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 공개 엔드포인트(로그인/콜백/로그아웃, WS 핸드셰이크, 헬스체크 등)
                        .requestMatchers(
                                "/auth/kakao/login",
                                "/auth/kakao/callback",
                                "/auth/logout",
                                "/ws/**",
                                "/files/**",
                                "/error",
                                "/favicon.ico",
                                "/actuator/**",
                                "/ok"
                        ).permitAll()

                        // 나머지는 인증 필요 (/auth/me, /auth/firebase/token 등 보호)
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // 패턴 기반 오리진 허용 (서브도메인/포트 변동 대응)
        var origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim).filter(s -> !s.isBlank()).toList();
        cfg.setAllowedOriginPatterns(origins);

        cfg.setAllowCredentials(true);
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*")); // 커스텀 헤더 대응
        cfg.setExposedHeaders(List.of("Set-Cookie","Authorization","Location"));
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}