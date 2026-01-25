package com.example.todojustforfun.services;

import com.example.todojustforfun.models.Group;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class GroupAuthorizationServiceTest {

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
    void groupDetails_allowsMember_andBlocksNonMember() {
        User owner = createUser("owner@example.com");
        User member = createUser("member@example.com");
        User outsider = createUser("outsider@example.com");

        Group group = groupService.createGroup("Team", owner.getId());
        groupMemberService.joinByCode(group.getJoinCode(), member.getId());

        Group result = groupService.getGroupDetails(group.getId(), member.getId());

        assertThat(result.getId()).isEqualTo(group.getId());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> groupService.getGroupDetails(group.getId(), outsider.getId())
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void listMembers_allowsOwner_andBlocksNonOwner() {
        User owner = createUser("owner2@example.com");
        User member = createUser("member2@example.com");

        Group group = groupService.createGroup("Project", owner.getId());
        groupMemberService.joinByCode(group.getJoinCode(), member.getId());

        List<?> members = groupMemberService.listMembers(group.getId(), owner.getId());

        assertThat(members).hasSize(2);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> groupMemberService.listMembers(group.getId(), member.getId())
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shareTodo_enforcesOwner_andSharedListsRequireMembership() {
        User owner = createUser("owner3@example.com");
        User member = createUser("member3@example.com");
        User outsider = createUser("outsider3@example.com");

        Group group = groupService.createGroup("Crew", owner.getId());
        groupMemberService.joinByCode(group.getJoinCode(), member.getId());

        Todo todo = createTodo(owner.getId(), "Shared task");

        groupTodoShareService.shareTodo(group.getId(), todo.getId(), owner.getId());

        List<Todo> sharedForMember = groupTodoShareService.listSharedTodos(group.getId(), member.getId(), null);
        assertThat(sharedForMember).hasSize(1);

        ResponseStatusException nonMemberListException = assertThrows(
                ResponseStatusException.class,
                () -> groupTodoShareService.listSharedTodos(group.getId(), outsider.getId(), null)
        );
        assertThat(nonMemberListException.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        ResponseStatusException nonOwnerShareException = assertThrows(
                ResponseStatusException.class,
                () -> groupTodoShareService.shareTodo(group.getId(), todo.getId(), member.getId())
        );
        assertThat(nonOwnerShareException.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        ResponseStatusException nonOwnerUnshareException = assertThrows(
                ResponseStatusException.class,
                () -> groupTodoShareService.unshareTodo(group.getId(), todo.getId(), member.getId())
        );
        assertThat(nonOwnerUnshareException.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        groupTodoShareService.unshareTodo(group.getId(), todo.getId(), owner.getId());

        List<Todo> sharedAfterUnshare = groupTodoShareService.listSharedTodos(group.getId(), owner.getId(), null);
        assertThat(sharedAfterUnshare).isEmpty();
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
}
