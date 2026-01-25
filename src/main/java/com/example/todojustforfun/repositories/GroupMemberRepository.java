package com.example.todojustforfun.repositories;

import com.example.todojustforfun.models.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
}
