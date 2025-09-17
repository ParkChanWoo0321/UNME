// com/example/uni/match/SignalRepository.java
package com.example.uni.match;

import com.example.uni.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SignalRepository extends JpaRepository<Signal, Long> {
    Optional<Signal> findBySenderAndReceiver(User sender, User receiver);
    List<Signal> findAllBySenderOrderByCreatedAtDesc(User sender);
    List<Signal> findAllByReceiverAndReceiverDeletedAtIsNullOrderByCreatedAtDesc(User receiver);
    List<Signal> findAllBySender(User sender);
    void deleteBySenderAndReceiver(User sender, User receiver);

}
