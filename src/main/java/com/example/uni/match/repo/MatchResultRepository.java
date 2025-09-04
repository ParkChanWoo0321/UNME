package com.example.uni.match.repo;

import com.example.uni.match.domain.MatchResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MatchResultRepository extends JpaRepository<MatchResult, UUID> { }
