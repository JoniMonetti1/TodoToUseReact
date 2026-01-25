package com.example.todojustforfun.services;

import com.example.todojustforfun.models.GroupMember;

import java.util.List;

public interface GroupMemberService {
    List<GroupMember> listMembers(Long groupId, Long requesterId);

    GroupMember joinByCode(String joinCode, Long userId);
}
