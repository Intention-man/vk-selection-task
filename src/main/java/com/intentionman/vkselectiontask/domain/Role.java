package com.intentionman.vkselectiontask.domain;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_USERS, ROLE_POSTS, ROLE_ALBUMS, ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}