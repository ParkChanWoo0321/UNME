// match/repo/SignalRepository.java  (신규)
package com.example.uni.match.repo;

import com.example.uni.match.domain.Signal;
import com.example.uni.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SignalRepository extends JpaRepository<Signal, UUID> {
    Optional<Signal> findBySenderAndReceiver(User sender, User receiver);

    List<Signal> findAllBySenderOrderByCreatedAtDesc(User sender);
    List<Signal> findAllByReceiverOrderByCreatedAtDesc(User receiver);

    List<Signal> findAllBySenderAndStatusOrderByCreatedAtDesc(User sender, Signal.Status status);
    List<Signal> findAllByReceiverAndStatusOrderByCreatedAtDesc(User receiver, Signal.Status status);

    // 보낸 전체(필터링용)
    List<Signal> findAllBySender(User sender);
}
