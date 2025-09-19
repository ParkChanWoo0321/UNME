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
select mbti, count(*) as cnt
from (
  select upper(u.mbti) as mbti
  from signal_logs l
  join users u on u.id = l.receiver_id
  where u.deactivated_at is null and u.mbti is not null and u.mbti <> ''
) t
group by mbti
order by cnt desc, mbti asc
""", nativeQuery = true)
    List<Object[]> countByReceiverMbti();
}
