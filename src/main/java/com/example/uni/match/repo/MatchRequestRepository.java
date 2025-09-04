package com.example.uni.match.repo;

import com.example.uni.match.domain.MatchRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MatchRequestRepository extends JpaRepository<MatchRequest, UUID> { }
