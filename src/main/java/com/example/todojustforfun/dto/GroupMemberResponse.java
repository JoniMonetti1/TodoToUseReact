package com.example.todojustforfun.dto;

import java.time.Instant;

public record GroupMemberResponse(
        Long id,
        Long groupId,
        Long userId,
        Instant createdAt
) {
}
