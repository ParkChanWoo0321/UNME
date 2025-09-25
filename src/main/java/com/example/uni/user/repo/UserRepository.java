package com.example.uni.user.repo;

import com.example.uni.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKakaoId(String kakaoId);
    Optional<User> findByEmail(String email);
    boolean existsByNameIgnoreCase(String name);
    Optional<User> findByNameIgnoreCase(String name);
}
