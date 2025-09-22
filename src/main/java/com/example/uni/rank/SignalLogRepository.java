// com/example/uni/rank/SignalLogRepository.java
package com.example.uni.rank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SignalLogRepository extends JpaRepository<SignalLog, Long> {

    @Query(value = """
select receiver_department, count(*) as cnt
from signal_logs
where receiver_department is not null and receiver_department <> ''
group by receiver_department
order by cnt desc, receiver_department collate utf8mb4_0900_ai_ci asc
""", nativeQuery = true)
    List<Object[]> countByReceiverDepartment();

    @Query(value = """
select receiver_mbti as mbti, count(*) as cnt
from signal_logs
where receiver_mbti is not null and receiver_mbti <> ''
group by receiver_mbti
order by cnt desc, receiver_mbti asc
""", nativeQuery = true)
    List<Object[]> countByReceiverMbti();
}
