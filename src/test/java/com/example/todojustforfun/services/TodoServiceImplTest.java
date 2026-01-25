package com.example.todojustforfun.services;

import com.example.todojustforfun.dto.TodoRequest;
import com.example.todojustforfun.dto.TodoResponse;
import com.example.todojustforfun.mapper.TodoMapper;
import com.example.todojustforfun.models.Todo;
import com.example.todojustforfun.repositories.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    @Mock
    private TodoRepository todoRepository;

    private TodoServiceImpl todoService;

    @BeforeEach
    void setUp() {
        todoService = new TodoServiceImpl(todoRepository, new TodoMapper());
    }

    @Test
    void createTodo_allowsNullDueDate() {
        TodoRequest request = new TodoRequest("Study", "Read book", null);

        when(todoRepository.existsByTitleIgnoreCase("Study")).thenReturn(false);
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TodoResponse response = todoService.createTodo(request, 10L);

        assertThat(response.dueDate()).isNull();
    }

    @Test
    void createTodo_acceptsFutureOffsetDueDate() {
        OffsetDateTime dueDate = OffsetDateTime.now(ZoneOffset.ofHours(-3)).plusDays(1);
        TodoRequest request = new TodoRequest("Workout", "Leg day", dueDate);

        when(todoRepository.existsByTitleIgnoreCase("Workout")).thenReturn(false);
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TodoResponse response = todoService.createTodo(request, 11L);

        assertThat(response.dueDate()).isEqualTo(dueDate);
    }

    @Test
    void createTodo_rejectsPastOffsetDueDate() {
        OffsetDateTime dueDate = OffsetDateTime.now(ZoneOffset.UTC).minusDays(1);
        TodoRequest request = new TodoRequest("Plan", "Write notes", dueDate);

        assertThatThrownBy(() -> todoService.createTodo(request, 12L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
