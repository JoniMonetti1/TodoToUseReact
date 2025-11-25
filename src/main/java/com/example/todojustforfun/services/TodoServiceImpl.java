package com.example.todojustforfun.services;

import com.example.todojustforfun.dto.TodoRequest;
import com.example.todojustforfun.dto.TodoResponse;
import com.example.todojustforfun.mapper.TodoMapper;
import com.example.todojustforfun.models.Todo;
import com.example.todojustforfun.repositories.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class TodoServiceImpl implements TodoService{

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;

    public TodoServiceImpl(TodoRepository todoRepository, TodoMapper todoMapper) {
        this.todoRepository = todoRepository;
        this.todoMapper = todoMapper;
    }

    @Override
    public List<TodoResponse> getAllTodos() {
        return todoRepository.findAll()
                .stream()
                .map(todoMapper::toResponse)
                .toList();
    }

    @Override
    public List<TodoResponse> getAllTodosByTitle(String title) {
        return todoRepository.findByTitleIgnoreCaseContaining(title)
                .stream()
                .map(todoMapper::toResponse)
                .toList();
    }

    @Override
    public TodoResponse getTodoById(Long id) {
        return todoRepository.findById(id)
                .map(todoMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Todo not found with id: " + id));
    }

    @Override
    @Transactional
    public TodoResponse createTodo(TodoRequest request) {
        Todo todo = todoMapper.toEntity(request);

        return validateTodoData(todo)
                .flatMap(this::checkTitleUniqueness)
                .map(todoRepository::save)
                .map(todoMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Invalid todo data or title already exists"));
    }

    @Override
    @Transactional
    public TodoResponse updateTodo(Long id, TodoRequest request) {
        Todo todoDetails = todoMapper.toEntity(request);

        return todoRepository.findById(id)
                .flatMap(existingTodo -> validateTodoData(todoDetails)
                        .flatMap(validated -> checkTitleUniquenesForUpdate(validated, id))
                        .map(validated -> updateTodoFields(existingTodo, validated)))
                .map(todoRepository::save)
                .map(todoMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Todo not found or invalid data"));
    }

    @Override
    @Transactional
    public void deleteTodoById(Long id) {
        todoRepository.findById(id)
                .ifPresentOrElse(
                        todoRepository::delete,
                        () -> {throw new IllegalArgumentException("Todo not found with id: " + id); }
                );
    }

    private Optional<Todo> validateTodoData(Todo todo) {
        Predicate<Todo> hasTitle = t -> t.getTitle() != null && !t.getTitle().trim().isEmpty();
        Predicate<Todo> hasDescription = t -> t.getDescription() != null && !t.getDescription().trim().isEmpty();

        return Optional.of(todo)
                .filter(hasTitle.and(hasDescription));
    }

    private Optional<Todo> checkTitleUniqueness(Todo todo) {
        return todoRepository.existsByTitleIgnoreCase(todo.getTitle())
                ? Optional.empty()
                : Optional.of(todo);
    }

    private Optional<Todo> checkTitleUniquenesForUpdate(Todo todo, Long currentId) {
        return todoRepository.findByTitleIgnoreCase(todo.getTitle())
                .filter(existing -> !existing.getId().equals(currentId))
                .map(existing -> (Todo) null)
                .or(() -> Optional.of(todo));
    }

    private Todo updateTodoFields(Todo existing, Todo updated) {
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        return existing;
    }
}
