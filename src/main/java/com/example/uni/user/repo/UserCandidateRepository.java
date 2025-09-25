package com.example.uni.user.repo;

import com.example.uni.user.domain.Gender;
import com.example.uni.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCandidateRepository extends JpaRepository<User, Long> {
    @Query("""
    select u from User u
    where u.gender = :gender
      and ( :dept is null or u.department is null or u.department <> :dept )
      and u.profileComplete = true
      and u.deactivatedAt is null
      and u.id <> :meId
      and not exists (
        select 1 from SeenCandidate sc
        where sc.viewerId = :meId and sc.seenUserId = u.id
      )
    """)
    List<User> findCandidates(@Param("gender") Gender gender,
                              @Param("dept") String dept,
                              @Param("meId") Long meId);
}
