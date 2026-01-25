package com.example.todojustforfun.controllers;

import com.example.todojustforfun.dto.TodoRequest;
import com.example.todojustforfun.models.Todo;
import com.example.todojustforfun.models.User;
import com.example.todojustforfun.repositories.GroupMemberRepository;
import com.example.todojustforfun.repositories.GroupRepository;
import com.example.todojustforfun.repositories.GroupTodoShareRepository;
import com.example.todojustforfun.repositories.TodoRepository;
import com.example.todojustforfun.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class TodoOwnershipControllerTest {

    @Autowired
    private TodoController todoController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private GroupTodoShareRepository groupTodoShareRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private GroupRepository groupRepository;

    private Todo ownerTodo;

    @BeforeEach
    void setUp() {
        groupTodoShareRepository.deleteAll();
        groupMemberRepository.deleteAll();
        groupRepository.deleteAll();
        todoRepository.deleteAll();
        userRepository.deleteAll();

        User owner = new User();
        owner.setEmail("owner@example.com");
        owner.setPasswordHash("hashed-password");
        owner = userRepository.save(owner);

        User other = new User();
        other.setEmail("other@example.com");
        other.setPasswordHash("hashed-password");
        userRepository.save(other);

        Todo todo = new Todo();
        todo.setTitle("Owner todo");
        todo.setDescription("Owner description");
        todo.setUserId(owner.getId());
        ownerTodo = todoRepository.save(todo);
    }

    @Test
    void updateTodo_returnsNotFoundForNonOwner() throws Exception {
        TodoRequest request = new TodoRequest("Updated title", "Updated description", null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "other@example.com",
                "password",
                java.util.List.of()
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> todoController.updateTodo(ownerTodo.getId(), request, authentication)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteTodo_returnsNotFoundForNonOwner() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "other@example.com",
                "password",
                java.util.List.of()
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> todoController.deleteTodoById(ownerTodo.getId(), authentication)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getTodo_returnsNotFoundForNonOwner() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "other@example.com",
                "password",
                java.util.List.of()
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> todoController.getTodoById(ownerTodo.getId(), authentication)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
