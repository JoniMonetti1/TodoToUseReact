package com.example.todojustforfun.repositories;

import com.example.todojustforfun.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByJoinCode(String joinCode);

    boolean existsByJoinCode(String joinCode);
}
