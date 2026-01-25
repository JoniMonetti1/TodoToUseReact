package com.example.todojustforfun.controllers;

import com.example.todojustforfun.dto.TodoRequest;
import com.example.todojustforfun.dto.TodoResponse;
import com.example.todojustforfun.dto.UserResponse;
import com.example.todojustforfun.services.AuthService;
import com.example.todojustforfun.services.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {
    private final TodoService todoService;
    private final AuthService authService;

    public TodoController(TodoService todoService, AuthService authService) {
        this.todoService = todoService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<TodoResponse>> getAllTodos(Authentication authentication) {
        UserResponse currentUser = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(todoService.getAllTodos(currentUser.id()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TodoResponse>> getAllTodosByTitle(@RequestParam String title, Authentication authentication) {
        UserResponse currentUser = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(todoService.getAllTodosByTitle(title, currentUser.id()));
    }

    @GetMapping("/completed")
    public ResponseEntity<List<TodoResponse>> getAllTodosByCompleted(@RequestParam Boolean completed, Authentication authentication) {
        UserResponse currentUser = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(todoService.getAllTodosByCompleted(completed, currentUser.id()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getTodoById(@PathVariable Long id, Authentication authentication) {
        UserResponse currentUser = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(todoService.getTodoById(id, currentUser.id()));
    }

    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@Valid @RequestBody TodoRequest request, Authentication authentication) {
        UserResponse currentUser = authService.getCurrentUser(authentication);
        TodoResponse createdTodo = todoService.createTodo(request, currentUser.id());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTodo.id())
                .toUri();

        return ResponseEntity.created(location).body(createdTodo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(@PathVariable Long id, @RequestBody TodoRequest request, Authentication authentication) {
        UserResponse currentUser = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(todoService.updateTodo(id, currentUser.id(), request));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<TodoResponse> completeTodo(@PathVariable Long id, Authentication authentication) {
        UserResponse currentUser = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(todoService.completeTodo(id, currentUser.id()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodoById(@PathVariable Long id, Authentication authentication) {
        UserResponse currentUser = authService.getCurrentUser(authentication);
        todoService.deleteTodoById(id, currentUser.id());
        return ResponseEntity.noContent().build();
    }
}
