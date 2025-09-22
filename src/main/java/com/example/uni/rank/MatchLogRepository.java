// com/example/uni/rank/MatchLogRepository.java
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
          select mbti_a as mbti from match_logs
          union all
          select mbti_b as mbti from match_logs
        ) t
        where mbti is not null and mbti <> ''
        group by mbti
        order by matchedCount desc, mbti asc
        """, nativeQuery = true)
    List<Object[]> rankMbtiByMatches();
}
