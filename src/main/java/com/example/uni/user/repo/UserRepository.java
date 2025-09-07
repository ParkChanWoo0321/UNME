package com.example.uni.user.repo;

import com.example.uni.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByKakaoId(String kakaoId);
    boolean existsByNameIgnoreCase(String name);
    Optional<User> findByNameIgnoreCase(String name);
}