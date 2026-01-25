package com.example.todojustforfun.services;

import com.example.todojustforfun.dto.TodoRequest;
import com.example.todojustforfun.dto.TodoResponse;

import java.util.List;

public interface TodoService {
    List<TodoResponse> getAllTodos(Long userId);

    List<TodoResponse> getAllTodosByTitle(String title, Long userId);

    List<TodoResponse> getAllTodosByCompleted(Boolean completed, Long userId);

    TodoResponse getTodoById(Long id, Long userId);

    TodoResponse createTodo(TodoRequest request, Long userId);

    TodoResponse updateTodo(Long id, Long userId, TodoRequest request);

    TodoResponse completeTodo(Long id, Long userId);

    void deleteTodoById(Long id, Long userId);
}
