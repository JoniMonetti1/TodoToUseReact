package com.example.todojustforfun.mapper;

import com.example.todojustforfun.dto.TodoRequest;
import com.example.todojustforfun.dto.TodoResponse;
import com.example.todojustforfun.models.Todo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper class to convert between Todo entities and DTOs
 * This keeps entities isolated from the API layer
 */
@Component
public class TodoMapper {

    /**
     * Converts TodoRequest DTO to Todo entity
     * Used when creating or updating todos
     *
     * @param request The incoming TodoRequest from the API
     * @return A new Todo entity (without ID or createdAt - these are set by DB)
     */
    public Todo toEntity(TodoRequest request) {
        Todo todo = new Todo();
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setCompleted(false);
        return todo;
    }

    /**
     * Converts Todo entity to TodoResponse DTO
     * Used when sending data back to the client
     *
     * @param todo The Todo entity from the database
     * @return A TodoResponse DTO with all fields populated
     */
    public TodoResponse toResponse(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getCompleted(),
                todo.getCreatedAt()
        );
    }

    /**
     * Converts a list of Todo entities to a list of TodoResponse DTOs
     *
     * @param todos List of Todo entities
     * @return List of TodoResponse DTOs
     */
    public List<TodoResponse> toResponseList(List<Todo> todos) {
        return todos.stream()
                .map(this::toResponse)
                .toList();
    }
}
