// com/example/uni/match/SignalRepository.java
package com.example.uni.match;

import com.example.uni.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    // ▼ 내부 enum FQN 대신 파라미터로 전달
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update Signal s
              set s.status = :declined,
                  s.receiverDeletedAt = CURRENT_TIMESTAMP
            where s.status  = :sent
              and s.receiver = :u
           """)
    void declineAllIncomingFor(@Param("u") User u,
                              @Param("declined") Signal.Status declined,
                              @Param("sent") Signal.Status sent);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update Signal s
              set s.status = :declined
            where s.status = :sent
              and s.sender = :u
           """)
    void declineAllOutgoingFrom(@Param("u") User u,
                               @Param("declined") Signal.Status declined,
                               @Param("sent") Signal.Status sent);
}
