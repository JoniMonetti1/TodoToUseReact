package com.example.todojustforfun.services;

import com.example.todojustforfun.models.Group;
import com.example.todojustforfun.models.GroupMember;
import com.example.todojustforfun.repositories.GroupMemberRepository;
import com.example.todojustforfun.repositories.GroupRepository;
import com.example.todojustforfun.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class GroupMemberServiceImpl implements GroupMemberService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    public GroupMemberServiceImpl(
            GroupRepository groupRepository,
            GroupMemberRepository groupMemberRepository,
            UserRepository userRepository
    ) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
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

    @Override
    @Transactional
    public GroupMember addMember(Long groupId, Long userId, Long requesterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));

        if (!group.getOwnerId().equals(requesterId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only group owners can manage membership");
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseGet(() -> {
                    GroupMember member = new GroupMember();
                    member.setGroupId(groupId);
                    member.setUserId(userId);
                    return groupMemberRepository.save(member);
                });
    }

    @Override
    @Transactional
    public void removeMember(Long groupId, Long userId, Long requesterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));

        if (!group.getOwnerId().equals(requesterId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only group owners can manage membership");
        }

        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group member not found");
        }

        groupMemberRepository.deleteByGroupIdAndUserId(groupId, userId);
    }
}
