package com.example.todojustforfun.dto;

import java.time.Instant;

public record TodoResponse(
        Long id,
        String title,
        String description,
        Instant createdAt
) {
}
