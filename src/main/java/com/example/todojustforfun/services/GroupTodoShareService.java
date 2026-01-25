package com.example.todojustforfun.services;

import com.example.todojustforfun.models.GroupTodoShare;
import com.example.todojustforfun.models.Todo;

import java.util.List;

public interface GroupTodoShareService {
    GroupTodoShare shareTodo(Long groupId, Long todoId, Long requesterId);

    void unshareTodo(Long groupId, Long todoId, Long requesterId);

    List<Todo> listSharedTodos(Long groupId, Long requesterId, Long ownerId);
}
