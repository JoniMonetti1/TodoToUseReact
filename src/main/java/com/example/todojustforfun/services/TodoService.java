package com.example.todojustforfun.services;

import com.example.todojustforfun.models.Todo;

import java.util.List;
import java.util.Optional;

public interface TodoService {
    List<Todo> getAllTodos();
    List<Todo> getAllTodosByTitle(String title);
    Optional<Todo> getTodoById(Long id);
    Todo createTodo(Todo todo);
    Todo updateTodo(Long id, Todo todoDetails);
    void deleteTodoById(Long id);
}
