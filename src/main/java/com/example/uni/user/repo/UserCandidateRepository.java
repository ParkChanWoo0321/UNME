// com/example/uni/user/repo/UserCandidateRepository.java
package com.example.uni.user.repo;

import com.example.uni.user.domain.Gender;
import com.example.uni.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCandidateRepository extends JpaRepository<User, Long> {

    // ★ NULL 학과 허용 + 같은 학과만 배제 (me.department가 NULL이어도 후보 나옴)
    @Query("""
    select u from User u
    where u.gender = :gender
      and ( :dept is null or u.department is null or u.department <> :dept )
      and u.profileComplete = true
      and u.deactivatedAt is null
      and u.id <> :meId
    """)
    List<User> findCandidates(@Param("gender") Gender gender,
                              @Param("dept") String dept,
                              @Param("meId") Long meId);
}
