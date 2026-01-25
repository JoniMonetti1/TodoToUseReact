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
import java.util.UUID;

@Service
public class GroupServiceImpl implements GroupService {
    private static final int JOIN_CODE_LENGTH = 10;

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public GroupServiceImpl(GroupRepository groupRepository, GroupMemberRepository groupMemberRepository) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    @Override
    @Transactional
    public Group createGroup(String name, Long ownerId) {
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group name is required");
        }

        Group group = new Group();
        group.setName(name.trim());
        group.setOwnerId(ownerId);
        group.setJoinCode(generateJoinCode());

        Group saved = groupRepository.save(group);

        GroupMember ownerMember = new GroupMember();
        ownerMember.setGroupId(saved.getId());
        ownerMember.setUserId(ownerId);
        groupMemberRepository.save(ownerMember);

        return saved;
    }

    @Override
    public List<Group> listGroupsForUser(Long userId) {
        List<Long> groupIds = groupMemberRepository.findAllByUserId(userId)
                .stream()
                .map(GroupMember::getGroupId)
                .toList();

        if (groupIds.isEmpty()) {
            return List.of();
        }

        return groupRepository.findAllById(groupIds);
    }

    @Override
    public Group getGroupDetails(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));

        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a group member");
        }

        return group;
    }

    private String generateJoinCode() {
        for (int attempt = 0; attempt < 10; attempt++) {
            String candidate = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, JOIN_CODE_LENGTH);

            if (!groupRepository.existsByJoinCode(candidate)) {
                return candidate;
            }
        }

        throw new IllegalStateException("Unable to generate join code");
    }
}
