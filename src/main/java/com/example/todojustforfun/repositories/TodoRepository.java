package com.example.todojustforfun.repositories;

import com.example.todojustforfun.models.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByTitleIgnoreCaseContaining(String title);

    Optional<Todo> findByTitleIgnoreCase(String title);

    boolean existsByTitleIgnoreCase(String title);

    boolean existsByTitleIgnoreCaseAndIdNot(String title, Long id);

    List<Todo> findAllByCompleted(Boolean completed);
}
