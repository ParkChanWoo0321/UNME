// com/example/uni/user/repo/UserCandidateRepository.java
package com.example.uni.user.repo;

import com.example.uni.user.domain.Gender;
import com.example.uni.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCandidateRepository extends JpaRepository<User, Long> {
    /** 탈퇴자 제외 버전 */
    List<User> findByGenderAndDepartmentNotAndProfileCompleteTrueAndDeactivatedAtIsNullAndIdNot(
            Gender gender, String department, Long excludeId
    );
}
