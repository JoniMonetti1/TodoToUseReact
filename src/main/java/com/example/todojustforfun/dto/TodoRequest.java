package com.example.todojustforfun.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record TodoRequest(
        @NotBlank(message = "Title is requiered and cannot be blank")
        @Size(max = 120, message = "Title cannot be longer than 120 characters")
        String title,

        @NotBlank(message = "Description is requiered and cannot be blank")
        @Size(max = 255, message = "Description cannot be longer than 255 characters")
        String description,

        OffsetDateTime dueDate
) {
}
