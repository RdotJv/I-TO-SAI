package com.example.security_practice.repositories;

import com.example.security_practice.entities.DailyUseRequestId;
import com.example.security_practice.entities.DailyUserRequestCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyUserRequestCountRepository extends JpaRepository<DailyUserRequestCount, DailyUseRequestId> {
}
