package com.intentionman.vkselectiontask.domain.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;


// TODO как сделать доступ к созданию ROLE_ADMIN ?

public enum Role {
    ROLE_ADMIN,
    ROLE_USERS, ROLE_POSTS, ROLE_ALBUMS,
    ROLE_USERS_VIEWER, ROLE_POSTS_VIEWER, ROLE_ALBUMS_VIEWER,
    ROLE_DEFAULT;

    private final Set<Role> children = new HashSet<>();
    private final Set<String> availablePathRegex = new HashSet<>();
    private boolean canEditData = false;

    private static final Set<String> filterIgnorePaths = Set.of("/auth/.*", "/swagger-ui/.*", "/swagger-resources/.*", "/v3/api-docs/.*");
    static {
        ROLE_ADMIN.children.addAll(List.of(ROLE_POSTS, ROLE_ALBUMS, ROLE_USERS));
        ROLE_USERS.children.add(ROLE_USERS_VIEWER);
        ROLE_POSTS.children.add(ROLE_POSTS_VIEWER);
        ROLE_ALBUMS.children.add(ROLE_ALBUMS_VIEWER);
        ROLE_USERS_VIEWER.children.add(ROLE_DEFAULT);
        ROLE_POSTS_VIEWER.children.add(ROLE_DEFAULT);
        ROLE_ALBUMS_VIEWER.children.add(ROLE_DEFAULT);
    }

    static {
        ROLE_USERS_VIEWER.availablePathRegex.add("/users/.*");
        ROLE_POSTS_VIEWER.availablePathRegex.add("/posts(?:/.*)?");
        ROLE_ALBUMS_VIEWER.availablePathRegex.add("/albums/.*");
        ROLE_DEFAULT.availablePathRegex.addAll(filterIgnorePaths);
    }

    static {
        ROLE_ADMIN.canEditData = true;
        ROLE_USERS.canEditData = true;
        ROLE_POSTS.canEditData = true;
        ROLE_ALBUMS.canEditData = true;
    }

    public boolean includesRole(Role role) {
        return this.equals(role) || children.stream().anyMatch(r -> r.includesRole(role));
    }

    public boolean checkRequestPossibility(String method, String requestPath) {
        boolean containsRegPath = this.availablePathRegex
                .stream()
                .anyMatch(pathRegex -> Pattern.matches(pathRegex, requestPath));
        boolean canDoMethod = (method.equals("GET") || this.canEditData);
        return canDoMethod && (
                containsRegPath || children.stream().anyMatch(r -> r.checkRequestPossibility(method, requestPath))
        );
    }
}