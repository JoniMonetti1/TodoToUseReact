package com.example.todojustforfun.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GroupCreateRequest(
        @NotBlank
        @Size(max = 120)
        String name
) {
}
