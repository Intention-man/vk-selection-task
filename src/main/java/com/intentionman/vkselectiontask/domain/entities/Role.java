package com.intentionman.vkselectiontask.domain.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public enum Role {
    //TODO добавить больше ролей как в доп задании
    // TODO как сделать доступ к созданию ROLE_ADMIN ?
    ROLE_ADMIN,
    ROLE_USERS, ROLE_POSTS, ROLE_ALBUMS,
    ROLE_DEFAULT;

    private final Set<Role> children = new HashSet<>();

    private final Set<String> availablePathRegex = new HashSet<>();

    static {
        ROLE_ADMIN.children.addAll(List.of(ROLE_POSTS, ROLE_ALBUMS, ROLE_USERS));
        ROLE_POSTS.children.add(ROLE_DEFAULT);
        ROLE_ALBUMS.children.add(ROLE_DEFAULT);
        ROLE_USERS.children.add(ROLE_DEFAULT);
    }

    static {
        ROLE_POSTS.availablePathRegex.add("/posts(?:/.*)?");
        ROLE_ALBUMS.availablePathRegex.add("/albums/.*");
        ROLE_USERS.availablePathRegex.add("/users/.*");
        ROLE_DEFAULT.availablePathRegex.add("/auth/.*");
    }

    public boolean includesRole(Role role) {
        return this.equals(role) || children.stream().anyMatch(r -> r.includesRole(role));
    }

    public boolean checkRequestPossibility(String requestPath) {
        boolean containsRegPath = this.availablePathRegex.stream().anyMatch(pathRegex -> Pattern.matches(pathRegex, requestPath));
        return containsRegPath || children.stream().anyMatch(r -> r.checkRequestPossibility(requestPath));
    }
}