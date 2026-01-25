package com.example.todojustforfun.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GroupJoinRequest(
        @NotBlank
        @Size(max = 20)
        String joinCode
) {
}
