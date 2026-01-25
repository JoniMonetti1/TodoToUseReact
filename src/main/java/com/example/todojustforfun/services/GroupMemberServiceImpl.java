package com.example.todojustforfun.services;

import com.example.todojustforfun.models.Group;
import com.example.todojustforfun.models.GroupMember;
import com.example.todojustforfun.repositories.GroupMemberRepository;
import com.example.todojustforfun.repositories.GroupRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class GroupMemberServiceImpl implements GroupMemberService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public GroupMemberServiceImpl(GroupRepository groupRepository, GroupMemberRepository groupMemberRepository) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    @Override
    public List<GroupMember> listMembers(Long groupId, Long requesterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));

        if (!group.getOwnerId().equals(requesterId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only group owners can manage membership");
        }

        return groupMemberRepository.findAllByGroupId(groupId);
    }

    @Override
    @Transactional
    public GroupMember joinByCode(String joinCode, Long userId) {
        Group group = groupRepository.findByJoinCode(joinCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));

        return groupMemberRepository.findByGroupIdAndUserId(group.getId(), userId)
                .orElseGet(() -> {
                    GroupMember member = new GroupMember();
                    member.setGroupId(group.getId());
                    member.setUserId(userId);
                    return groupMemberRepository.save(member);
                });
    }
}
