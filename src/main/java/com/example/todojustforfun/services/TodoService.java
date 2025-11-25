package com.example.todojustforfun.services;

import com.example.todojustforfun.dto.TodoRequest;
import com.example.todojustforfun.dto.TodoResponse;
import com.example.todojustforfun.models.Todo;

import java.util.List;
import java.util.Optional;

public interface TodoService {
    List<TodoResponse> getAllTodos();
    List<TodoResponse> getAllTodosByTitle(String title);
    TodoResponse getTodoById(Long id);
    TodoResponse createTodo(TodoRequest request);
    TodoResponse updateTodo(Long id, TodoRequest request);
    void deleteTodoById(Long id);
}
