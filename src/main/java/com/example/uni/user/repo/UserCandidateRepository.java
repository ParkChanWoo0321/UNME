package com.example.uni.user.repo;

import com.example.uni.user.domain.Gender;
import com.example.uni.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserCandidateRepository extends JpaRepository<User, UUID> {
    List<User> findByGenderAndDepartmentNotAndProfileCompleteTrueAndIdNot(
            Gender gender, String department, UUID excludeId
    );
}