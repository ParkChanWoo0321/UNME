package com.example.uni.match;

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

    List<Signal> findAllBySender(User sender);
}
