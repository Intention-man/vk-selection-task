package com.intentionman.vkselectiontask.domain.entities;

public enum Role {
    ROLE_ADMIN,
    ROLE_USERS, ROLE_POSTS, ROLE_ALBUMS,
    ROLE_DEFAULT;
    //TODO сделать иерархию
    //TODO добавить больше ролей как в доп задании
    // TODO как сделать доступ к созданию ROLE_ADMIN ?
}