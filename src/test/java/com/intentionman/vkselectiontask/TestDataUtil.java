package com.intentionman.vkselectiontask;

import com.intentionman.vkselectiontask.domain.entities.Role;
import com.intentionman.vkselectiontask.domain.entities.UserEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class TestDataUtil {

    private TestDataUtil(){
    }

    public static UserEntity createTestAdminRole() {
        return new UserEntity(100L, "sysadmin", "123456", Role.ROLE_ADMIN);
    }

    public static UserEntity createTestUsersRole() {
        return new UserEntity(400L, "leader", "123456", Role.ROLE_USERS);
    }

    public static UserEntity createTestPostsRole() {
        return new UserEntity(200L, "postman", "123456", Role.ROLE_POSTS);
    }

    public static UserEntity createTestAlbumsRole() {
        return new UserEntity(300L, "photographer", "123456", Role.ROLE_ALBUMS);
    }

    public static UserEntity createTestUsersViewerRole() {
        return new UserEntity(200L, "teammate", "123456", Role.ROLE_USERS_VIEWER);
    }

    public static UserEntity createTestPostsViewerRole() {
        return new UserEntity(200L, "postmanReader", "123456", Role.ROLE_POSTS_VIEWER);
    }

    public static UserEntity createTestAlbumsViewerRole() {
        return new UserEntity(200L, "photographerReader", "123456", Role.ROLE_ALBUMS_VIEWER);
    }

    // UNUSED in proxy tests

    public static UserEntity createTestDefaultRole() {
        return new UserEntity(500L, "somebody", "123456", Role.ROLE_DEFAULT);
    }

    public static UserEntity createTooShortUser() {
        return new UserEntity(600L, "short", "pass", Role.ROLE_DEFAULT);
    }

    public static Map<Role, UserEntity> createTestUsers() {
        Map<Role, UserEntity> roleToTestUserMap = new HashMap<>();
        roleToTestUserMap.put(Role.ROLE_ADMIN, createTestAdminRole());
        roleToTestUserMap.put(Role.ROLE_USERS, createTestUsersRole());
        roleToTestUserMap.put(Role.ROLE_POSTS, createTestPostsRole());
        roleToTestUserMap.put(Role.ROLE_ALBUMS, createTestAlbumsRole());
        roleToTestUserMap.put(Role.ROLE_USERS_VIEWER, createTestUsersViewerRole());
        roleToTestUserMap.put(Role.ROLE_POSTS_VIEWER, createTestPostsViewerRole());
        roleToTestUserMap.put(Role.ROLE_ALBUMS_VIEWER, createTestAlbumsViewerRole());

        return roleToTestUserMap;
    }

    public static Post createTestPost(){
        return new Post(1L, "post title", "post body");
    }

    public static Album createTestAlbum(){
        return new Album(1L, "album title");
    }


    public static class RequestData {
        public String method;
        public String path;

        public Object body;

        public RequestData(String method, String path) {
            this.method = method;
            this.path = path;
        }

        public RequestData(String method, String path, Object body) {
            this.method = method;
            this.path = path;
            this.body = body;
        }
    }

    public static class Album {
        public long id = 1;
        public long userId;
        public String title;

        public Album(long userId, String title) {
            this.userId = userId;
            this.title = title;
        }
    }

    public static class Post {
        public long id = 1;
        public long userId;
        public String title;
        public String body;

        public Post(long userId, String title, String body) {
            this.userId = userId;
            this.title = title;
            this.body = body;
        }
    }

}