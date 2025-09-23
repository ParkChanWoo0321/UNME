package com.example.uni.push;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscriptionEntity, Long> {
    Optional<PushSubscriptionEntity> findByUserId(Long userId);
}
