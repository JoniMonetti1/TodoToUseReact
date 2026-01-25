package com.example.todojustforfun.services;

import com.example.todojustforfun.models.GroupTodoShare;
import com.example.todojustforfun.models.Todo;
import com.example.todojustforfun.repositories.GroupMemberRepository;
import com.example.todojustforfun.repositories.GroupRepository;
import com.example.todojustforfun.repositories.GroupTodoShareRepository;
import com.example.todojustforfun.repositories.TodoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class GroupTodoShareServiceImpl implements GroupTodoShareService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupTodoShareRepository groupTodoShareRepository;
    private final TodoRepository todoRepository;

    public GroupTodoShareServiceImpl(
            GroupRepository groupRepository,
            GroupMemberRepository groupMemberRepository,
            GroupTodoShareRepository groupTodoShareRepository,
            TodoRepository todoRepository
    ) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.groupTodoShareRepository = groupTodoShareRepository;
        this.todoRepository = todoRepository;
    }

    @Override
    @Transactional
    public GroupTodoShare shareTodo(Long groupId, Long todoId, Long requesterId) {
        ensureGroupExists(groupId);
        ensureMember(groupId, requesterId);

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));

        if (!todo.getUserId().equals(requesterId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only todo owners can share");
        }

        return groupTodoShareRepository.findByGroupIdAndTodoId(groupId, todoId)
                .orElseGet(() -> {
                    GroupTodoShare share = new GroupTodoShare();
                    share.setGroupId(groupId);
                    share.setTodoId(todoId);
                    return groupTodoShareRepository.save(share);
                });
    }

    @Override
    @Transactional
    public void unshareTodo(Long groupId, Long todoId, Long requesterId) {
        ensureGroupExists(groupId);
        ensureMember(groupId, requesterId);

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));

        if (!todo.getUserId().equals(requesterId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only todo owners can unshare");
        }

        groupTodoShareRepository.deleteByGroupIdAndTodoId(groupId, todoId);
    }

    @Override
    public List<Todo> listSharedTodos(Long groupId, Long requesterId, Long ownerId) {
        ensureGroupExists(groupId);
        ensureMember(groupId, requesterId);

        List<Long> todoIds = groupTodoShareRepository.findAllByGroupId(groupId)
                .stream()
                .map(GroupTodoShare::getTodoId)
                .toList();

        if (todoIds.isEmpty()) {
            return List.of();
        }

        List<Todo> todos = todoRepository.findAllById(todoIds);

        if (ownerId == null) {
            return todos;
        }

        return todos.stream()
                .filter(todo -> ownerId.equals(todo.getUserId()))
                .toList();
    }

    private void ensureGroupExists(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found");
        }
    }

    private void ensureMember(Long groupId, Long userId) {
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a group member");
        }
    }
}
