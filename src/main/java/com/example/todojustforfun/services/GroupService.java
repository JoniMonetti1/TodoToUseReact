package com.example.todojustforfun.services;

import com.example.todojustforfun.models.Group;

import java.util.List;

public interface GroupService {
    Group createGroup(String name, Long ownerId);

    List<Group> listGroupsForUser(Long userId);

    Group getGroupDetails(Long groupId, Long userId);
}
