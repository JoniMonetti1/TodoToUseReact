package com.example.todojustforfun.services;

import com.example.todojustforfun.dto.TodoRequest;
import com.example.todojustforfun.dto.TodoResponse;
import com.example.todojustforfun.mapper.TodoMapper;
import com.example.todojustforfun.models.Todo;
import com.example.todojustforfun.repositories.TodoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;

    public TodoServiceImpl(TodoRepository todoRepository, TodoMapper todoMapper) {
        this.todoRepository = todoRepository;
        this.todoMapper = todoMapper;
    }

    @Override
    public List<TodoResponse> getAllTodos(Long userId) {
        return todoRepository.findAllByUserId(userId)
                .stream()
                .map(todoMapper::toResponse)
                .toList();
    }

    @Override
    public List<TodoResponse> getAllTodosByTitle(String title, Long userId) {
        return todoRepository.findByUserIdAndTitleIgnoreCaseContaining(userId, title)
                .stream()
                .map(todoMapper::toResponse)
                .toList();
    }

    @Override
    public List<TodoResponse> getAllTodosByCompleted(Boolean completed, Long userId) {
        return todoRepository.findAllByUserIdAndCompleted(userId, completed)
                .stream()
                .map(todoMapper::toResponse)
                .toList();
    }

    @Override
    public TodoResponse getTodoById(Long id, Long userId) {
        return todoRepository.findByIdAndUserId(id, userId)
                .map(todoMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found with id: " + id));
    }

    @Override
    @Transactional
    public TodoResponse createTodo(TodoRequest request, Long userId) {
        Todo todo = todoMapper.toEntity(request);
        todo.setUserId(userId);

        return validateTodoData(todo)
                .flatMap(this::checkTitleUniqueness)
                .map(todoRepository::save)
                .map(todoMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Invalid todo data or title already exists"));
    }

    @Override
    @Transactional
    public TodoResponse updateTodo(Long id, Long userId, TodoRequest request) {
        Todo todoDetails = todoMapper.toEntity(request);

        Todo existingTodo = todoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found with id: " + id));

        return validateTodoData(todoDetails)
                .flatMap(validated -> checkTitleUniquenesForUpdate(validated, id))
                .map(validated -> updateTodoFields(existingTodo, validated))
                .map(todoRepository::save)
                .map(todoMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Invalid todo data"));
    }

    @Override
    @Transactional
    public TodoResponse completeTodo(Long id, Long userId) {
        return todoRepository.findByIdAndUserId(id, userId)
                .map(todo -> {
                    var currentStatus = Boolean.TRUE.equals(todo.getCompleted());
                    todo.setCompleted(!currentStatus);
                    return todoRepository.save(todo);
                })
                .map(todoMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteTodoById(Long id, Long userId) {
        Todo todo = todoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found with id: " + id));

        todoRepository.delete(todo);
    }

    private Optional<Todo> validateTodoData(Todo todo) {
        Predicate<Todo> hasTitle = t -> t.getTitle() != null && !t.getTitle().trim().isEmpty();
        Predicate<Todo> hasDescription = t -> t.getDescription() != null && !t.getDescription().trim().isEmpty();
        Predicate<Todo> dueDateNotPast = t -> {
            if (t.getDueDate() == null) {
                return true;
            }
            Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
            Instant dueInstant = t.getDueDate().toInstant().truncatedTo(ChronoUnit.SECONDS);
            return !dueInstant.isBefore(now);
        };

        return Optional.of(todo)
                .filter(hasTitle.and(hasDescription).and(dueDateNotPast));
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
        existing.setDueDate(updated.getDueDate());
        return existing;
    }
}
