package com.intentionman.vkselectiontask;

import com.intentionman.vkselectiontask.domain.entities.Role;
import com.intentionman.vkselectiontask.domain.entities.UserEntity;

import java.util.ArrayList;
import java.util.List;


public final class TestDataUtil {

    private TestDataUtil(){
    }

    public static UserEntity createTestAdminRole() {
        return new UserEntity(100L, "sysadmin", "123456", Role.ROLE_ADMIN);
    }

    public static UserEntity createTestPostsRole() {
        return new UserEntity(200L, "postman", "123456", Role.ROLE_POSTS);
    }

    public static UserEntity createTestAlbumsRole() {
        return new UserEntity(300L, "photographer", "123456", Role.ROLE_ALBUMS);
    }

    public static UserEntity createTestUsersRole() {
        return new UserEntity(400L, "leader", "123456", Role.ROLE_USERS);
    }

    public static UserEntity createTestDefaultRole() {
        return new UserEntity(500L, "somebody", "123456", Role.ROLE_DEFAULT);
    }

    public static UserEntity createTooShortUser() {
        return new UserEntity(600L, "short", "pass", Role.ROLE_DEFAULT);
    }

    public static List<UserEntity> createTestUsers() {
        List<UserEntity> userEntities = new ArrayList<>();
        userEntities.add(createTestAdminRole());
        userEntities.add(createTestPostsRole());
        userEntities.add(createTestAlbumsRole());
        userEntities.add(createTestUsersRole());
        userEntities.add(createTestDefaultRole());
        return userEntities;
    }

}