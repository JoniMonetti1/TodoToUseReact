package com.example.todojustforfun.services;

import com.example.todojustforfun.dto.TodoRequest;
import com.example.todojustforfun.dto.TodoResponse;

import java.util.List;

public interface TodoService {
    List<TodoResponse> getAllTodos();

    List<TodoResponse> getAllTodosByTitle(String title);

    List<TodoResponse> getAllTodosByCompleted(Boolean completed);

    TodoResponse getTodoById(Long id);

    TodoResponse createTodo(TodoRequest request, Long userId);

    TodoResponse updateTodo(Long id, TodoRequest request);

    TodoResponse completeTodo(Long id);

    void deleteTodoById(Long id);
}
