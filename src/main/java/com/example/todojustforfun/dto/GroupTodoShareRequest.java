package com.example.todojustforfun.dto;

import jakarta.validation.constraints.NotNull;

public record GroupTodoShareRequest(
        @NotNull
        Long todoId
) {
}
