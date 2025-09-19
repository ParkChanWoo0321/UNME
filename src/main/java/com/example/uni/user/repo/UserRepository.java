package com.example.uni.user.repo;

import com.example.uni.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKakaoId(String kakaoId);
    boolean existsByNameIgnoreCase(String name);
    Optional<User> findByNameIgnoreCase(String name);

    long countByDeactivatedAtIsNull();

    @Query("""
           select upper(trim(u.egenType)), count(u)
           from User u
           where u.deactivatedAt is null
             and u.egenType is not null
             and trim(u.egenType) <> ''
           group by upper(trim(u.egenType))
           """)
    List<Object[]> countActiveByEgenType();
}
