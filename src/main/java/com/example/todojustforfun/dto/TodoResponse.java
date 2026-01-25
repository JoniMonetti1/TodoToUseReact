package com.example.todojustforfun.dto;

import java.time.Instant;
import java.time.OffsetDateTime;

public record TodoResponse(
        Long id,
        String title,
        String description,
        Boolean completed,
        Instant createdAt,
        OffsetDateTime dueDate
) {
}
