package com.example.todojustforfun.controllers;

import com.example.todojustforfun.dto.GroupMemberAddRequest;
import com.example.todojustforfun.models.Group;
import com.example.todojustforfun.models.Todo;
import com.example.todojustforfun.models.User;
import com.example.todojustforfun.repositories.GroupMemberRepository;
import com.example.todojustforfun.repositories.GroupRepository;
import com.example.todojustforfun.repositories.GroupTodoShareRepository;
import com.example.todojustforfun.repositories.TodoRepository;
import com.example.todojustforfun.repositories.UserRepository;
import com.example.todojustforfun.services.GroupMemberService;
import com.example.todojustforfun.services.GroupService;
import com.example.todojustforfun.services.GroupTodoShareService;
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
class GroupControllerAuthTest {

    @Autowired
    private GroupController groupController;

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    private GroupTodoShareService groupTodoShareService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private GroupTodoShareRepository groupTodoShareRepository;

    @BeforeEach
    void setUp() {
        groupTodoShareRepository.deleteAll();
        groupMemberRepository.deleteAll();
        groupRepository.deleteAll();
        todoRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void listMembers_blocksNonOwner() {
        User owner = createUser("owner@example.com");
        User member = createUser("member@example.com");

        Group group = groupService.createGroup("Team", owner.getId());
        groupMemberService.joinByCode(group.getJoinCode(), member.getId());

        Authentication memberAuth = authenticationFor(member);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> groupController.listMembers(group.getId(), memberAuth)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void addMember_blocksNonOwner() {
        User owner = createUser("owner2@example.com");
        User member = createUser("member2@example.com");
        User outsider = createUser("outsider2@example.com");

        Group group = groupService.createGroup("Project", owner.getId());
        groupMemberService.joinByCode(group.getJoinCode(), member.getId());

        Authentication memberAuth = authenticationFor(member);
        GroupMemberAddRequest request = new GroupMemberAddRequest(outsider.getId());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> groupController.addMember(group.getId(), request, memberAuth)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void sharedTodos_blocksNonMembers() {
        User owner = createUser("owner3@example.com");
        User member = createUser("member3@example.com");
        User outsider = createUser("outsider3@example.com");

        Group group = groupService.createGroup("Crew", owner.getId());
        groupMemberService.joinByCode(group.getJoinCode(), member.getId());

        Todo todo = createTodo(owner.getId(), "Shared task");
        groupTodoShareService.shareTodo(group.getId(), todo.getId(), owner.getId());

        Authentication outsiderAuth = authenticationFor(outsider);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> groupController.getSharedTodos(group.getId(), null, outsiderAuth)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private User createUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("hashed-password");
        return userRepository.save(user);
    }

    private Todo createTodo(Long userId, String title) {
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setDescription("desc");
        todo.setUserId(userId);
        return todoRepository.save(todo);
    }

    private Authentication authenticationFor(User user) {
        return new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                "password",
                java.util.List.of()
        );
    }
}
