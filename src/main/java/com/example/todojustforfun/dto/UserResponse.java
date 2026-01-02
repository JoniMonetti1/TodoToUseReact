package com.example.todojustforfun.dto;

import java.time.Instant;

public record UserResponse(
    Long id,
    String email,
    Instant createdAt
) {}
