package com.example.todojustforfun.repositories;

import com.example.todojustforfun.models.GroupTodoShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupTodoShareRepository extends JpaRepository<GroupTodoShare, Long> {
    List<GroupTodoShare> findAllByGroupId(Long groupId);

    Optional<GroupTodoShare> findByGroupIdAndTodoId(Long groupId, Long todoId);

    boolean existsByGroupIdAndTodoId(Long groupId, Long todoId);

    void deleteByGroupIdAndTodoId(Long groupId, Long todoId);
}
