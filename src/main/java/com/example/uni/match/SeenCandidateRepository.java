package com.example.uni.match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface SeenCandidateRepository extends JpaRepository<SeenCandidate, Long> {
    @Query("select sc.seenUserId from SeenCandidate sc where sc.viewerId = :viewerId")
    Set<Long> findSeenIds(@Param("viewerId") Long viewerId);
    boolean existsByViewerIdAndSeenUserId(Long viewerId, Long seenUserId);
}
