package com.example.todojustforfun.dto;

import java.time.Instant;

public record GroupTodoShareResponse(
        Long id,
        Long groupId,
        Long todoId,
        Instant createdAt
) {
}
