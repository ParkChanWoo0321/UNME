// user/repo/UserCandidateRepository.java
package com.example.uni.user.repo;

import com.example.uni.user.domain.Gender;
import com.example.uni.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserCandidateRepository extends JpaRepository<User, UUID> {
    // 이성 + 같은 학과 제외 + 프로필 완료 + 본인 제외
    List<User> findByGenderAndDepartmentNotAndProfileCompleteTrueAndIdNot(
            Gender gender, String department, UUID excludeId
    );
}
