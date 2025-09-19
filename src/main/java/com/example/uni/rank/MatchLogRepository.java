package com.example.uni.rank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface MatchLogRepository extends JpaRepository<MatchLog, Long> {

    @Query(value = """
        SELECT dept AS department, COUNT(*) AS matchedCount
        FROM (
          SELECT department_a AS dept FROM match_logs
          UNION ALL
          SELECT department_b AS dept FROM match_logs
        ) t
        WHERE dept IS NOT NULL AND dept <> ''
        GROUP BY dept
        ORDER BY matchedCount DESC
        """, nativeQuery = true)
    List<Object[]> rankDepartmentsByMatches();

    @Query(value = """
        select mbti, count(*) as matchedCount
        from (
          select upper(u.mbti) as mbti
          from match_logs m
          join users u on u.id = m.user_a_id
          where u.deactivated_at is null and u.mbti is not null and u.mbti <> ''
          union all
          select upper(u.mbti) as mbti
          from match_logs m
          join users u on u.id = m.user_b_id
          where u.deactivated_at is null and u.mbti is not null and u.mbti <> ''
        ) t
        group by mbti
        order by matchedCount desc, mbti asc
        """, nativeQuery = true)
    List<Object[]> rankMbtiByMatches();
}
