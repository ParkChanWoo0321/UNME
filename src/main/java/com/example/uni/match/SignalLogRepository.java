// com/example/uni/match/SignalLogRepository.java
package com.example.uni.match;

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
}
