package com.example.todojustforfun.controllers;

import com.example.todojustforfun.dto.GroupCreateRequest;
import com.example.todojustforfun.dto.GroupJoinRequest;
import com.example.todojustforfun.dto.GroupMemberAddRequest;
import com.example.todojustforfun.dto.GroupMemberResponse;
import com.example.todojustforfun.dto.GroupResponse;
import com.example.todojustforfun.dto.GroupTodoShareRequest;
import com.example.todojustforfun.dto.GroupTodoShareResponse;
import com.example.todojustforfun.dto.TodoResponse;
import com.example.todojustforfun.dto.UserResponse;
import com.example.todojustforfun.mapper.TodoMapper;
import com.example.todojustforfun.models.Group;
import com.example.todojustforfun.models.GroupMember;
import com.example.todojustforfun.models.GroupTodoShare;
import com.example.todojustforfun.models.Todo;
import com.example.todojustforfun.services.AuthService;
import com.example.todojustforfun.services.GroupMemberService;
import com.example.todojustforfun.services.GroupService;
import com.example.todojustforfun.services.GroupTodoShareService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {
    private final GroupService groupService;
    private final GroupMemberService groupMemberService;
    private final GroupTodoShareService groupTodoShareService;
    private final AuthService authService;
    private final TodoMapper todoMapper;

    public GroupController(
            GroupService groupService,
            GroupMemberService groupMemberService,
            GroupTodoShareService groupTodoShareService,
            AuthService authService,
            TodoMapper todoMapper
    ) {
        this.groupService = groupService;
        this.groupMemberService = groupMemberService;
        this.groupTodoShareService = groupTodoShareService;
        this.authService = authService;
        this.todoMapper = todoMapper;
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> listGroups(Authentication authentication) {
        UserResponse currentUser = authService.getCurrentUser(authentication);
        List<Group> groups = groupService.listGroupsForUser(currentUser.id());
        return ResponseEntity.ok(groups.stream().map(this::toGroupResponse).toList());
    }

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(
            @Valid @RequestBody GroupCreateRequest request,
            Authentication authentication
    ) {
        UserResponse currentUser = authService.getCurrentUser(authentication);
        Group created = groupService.createGroup(request.name(), currentUser.id());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(toGroupResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> getGroup(@PathVariable Long id, Authentication authentication) {
        UserResponse currentUser = authService.getCurrentUser(authentication);
        Group group = groupService.getGroupDetails(id, currentUser.id());
        return ResponseEntity.ok(toGroupResponse(group));
    }

    @PostMapping("/join")
    public ResponseEntity<GroupMemberResponse> joinGroup(
            @Valid @RequestBody GroupJoinRequest request,
            Authentication authentication
    ) {
        UserResponse currentUser = authService.getCurrentUser(authentication);
        GroupMember member = groupMemberService.joinByCode(request.joinCode(), currentUser.id());
        return ResponseEntity.ok(toMemberResponse(member));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<GroupMemberResponse>> listMembers(@PathVariable Long id, Authentication authentication) {
        UserResponse currentUser = authService.getCurrentUser(authentication);
        List<GroupMember> members = groupMemberService.listMembers(id, currentUser.id());
        return ResponseEntity.ok(members.stream().map(this::toMemberResponse).toList());
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<GroupMemberResponse> addMember(
            @PathVariable Long id,
            @Valid @RequestBody GroupMemberAddRequest request,
            Authentication authentication
    ) {
        UserResponse currentUser = authService.getCurrentUser(authentication);
        GroupMember member = groupMemberService.addMember(id, request.userId(), currentUser.id());
        return ResponseEntity.ok(toMemberResponse(member));
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long id,
            @PathVariable Long userId,
            Authentication authentication
    ) {
        UserResponse currentUser = authService.getCurrentUser(authentication);
        groupMemberService.removeMember(id, userId, currentUser.id());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/shared-todos")
    public ResponseEntity<List<TodoResponse>> getSharedTodos(
            @PathVariable Long id,
            @RequestParam(required = false) Long ownerId,
            Authentication authentication
    ) {
        UserResponse currentUser = authService.getCurrentUser(authentication);
        List<Todo> todos = groupTodoShareService.listSharedTodos(id, currentUser.id(), ownerId);
        return ResponseEntity.ok(todoMapper.toResponseList(todos));
    }

    @PostMapping("/{id}/shared-todos")
    public ResponseEntity<GroupTodoShareResponse> shareTodo(
            @PathVariable Long id,
            @Valid @RequestBody GroupTodoShareRequest request,
            Authentication authentication
    ) {
        UserResponse currentUser = authService.getCurrentUser(authentication);
        GroupTodoShare share = groupTodoShareService.shareTodo(id, request.todoId(), currentUser.id());
        return ResponseEntity.ok(toShareResponse(share));
    }

    @DeleteMapping("/{id}/shared-todos/{todoId}")
    public ResponseEntity<Void> unshareTodo(
            @PathVariable Long id,
            @PathVariable Long todoId,
            Authentication authentication
    ) {
        UserResponse currentUser = authService.getCurrentUser(authentication);
        groupTodoShareService.unshareTodo(id, todoId, currentUser.id());
        return ResponseEntity.noContent().build();
    }

    private GroupResponse toGroupResponse(Group group) {
        return new GroupResponse(
                group.getId(),
                group.getName(),
                group.getOwnerId(),
                group.getJoinCode(),
                group.getCreatedAt()
        );
    }

    private GroupMemberResponse toMemberResponse(GroupMember member) {
        return new GroupMemberResponse(
                member.getId(),
                member.getGroupId(),
                member.getUserId(),
                member.getCreatedAt()
        );
    }

    private GroupTodoShareResponse toShareResponse(GroupTodoShare share) {
        return new GroupTodoShareResponse(
                share.getId(),
                share.getGroupId(),
                share.getTodoId(),
                share.getCreatedAt()
        );
    }
}
