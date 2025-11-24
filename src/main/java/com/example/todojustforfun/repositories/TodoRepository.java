package com.example.todojustforfun.repositories;

import com.example.todojustforfun.models.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByTitleIgnoreCaseContaining(String title);
    boolean existsByTitleIgnoreCase(String title);
}
