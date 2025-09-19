// com/example/uni/match/SignalLogRepository.java
package com.example.uni.match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SignalLogRepository extends JpaRepository<SignalLog, Long> {
    @Query("""
           select l.receiverDepartment, count(l)
           from SignalLog l
           where l.receiverDepartment is not null
             and l.receiverDepartment <> ''
           group by l.receiverDepartment
           order by count(l) desc
           """)
    List<Object[]> countByReceiverDepartment();
}
