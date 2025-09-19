// com/example/uni/match/SignalRepository.java
package com.example.uni.match;

import com.example.uni.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SignalRepository extends JpaRepository<Signal, Long> {
    Optional<Signal> findBySenderAndReceiver(User sender, User receiver);
    List<Signal> findAllBySenderOrderByCreatedAtDesc(User sender);
    List<Signal> findAllByReceiverAndReceiverDeletedAtIsNullOrderByCreatedAtDesc(User receiver);
    List<Signal> findAllBySender(User sender);
    void deleteBySenderAndReceiver(User sender, User receiver);

    @Query("""
           select s.receiver.department, count(s)
           from Signal s
           where s.status = :status
             and s.receiver.department is not null
             and s.receiver.department <> ''
           group by s.receiver.department
           order by count(s) desc
           """)
    List<Object[]> countReceivedByDepartment(@Param("status") Signal.Status status);
}
