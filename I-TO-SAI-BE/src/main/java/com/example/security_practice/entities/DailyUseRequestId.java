package com.example.security_practice.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class DailyUseRequestId {
    private String userId;
    private LocalDate date;
}
