package com.example.todojustforfun.dto;

import java.time.Instant;

public record GroupResponse(
        Long id,
        String name,
        Long ownerId,
        String joinCode,
        Instant createdAt
) {
}
