package com.example.todojustforfun.services;

import com.example.todojustforfun.models.GroupMember;

import java.util.List;

public interface GroupMemberService {
    List<GroupMember> listMembers(Long groupId, Long requesterId);

    GroupMember joinByCode(String joinCode, Long userId);

    GroupMember addMember(Long groupId, Long userId, Long requesterId);

    void removeMember(Long groupId, Long userId, Long requesterId);
}
