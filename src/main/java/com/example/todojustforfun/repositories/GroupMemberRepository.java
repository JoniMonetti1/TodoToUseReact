package com.example.todojustforfun.repositories;

import com.example.todojustforfun.models.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findAllByUserId(Long userId);

    List<GroupMember> findAllByGroupId(Long groupId);

    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);

    boolean existsByGroupIdAndUserId(Long groupId, Long userId);
}
