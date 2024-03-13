package com.intentionman.vkselectiontask.unit_tests;

import com.intentionman.vkselectiontask.domain.entities.Role;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class RolesAuthoritiesTest {
    Map<String, List<FirstRequestRow>> requestRowMap = new HashMap<>();

    @BeforeEach
    void setUp() {
        String GET = "GET";
        String POST = "POST";
        String PUT = "PUT";
        String DELETE = "DELETE";
        requestRowMap.put("ROLE_DEFAULT", List.of(
                new FirstRequestRow(GET, "/auth/registration"),
                new FirstRequestRow(GET, "/auth/login"),
                new FirstRequestRow(GET, "/v3/api-docs/")
        ));
        requestRowMap.put("ROLE_USERS_VIEWER", List.of(new FirstRequestRow(GET, "/users")));
        requestRowMap.put("ROLE_POSTS_VIEWER", List.of(new FirstRequestRow(GET, "/posts")));
        requestRowMap.put("ROLE_ALBUMS_VIEWER", List.of(new FirstRequestRow(GET, "/albums")));
        requestRowMap.put("ROLE_USERS", List.of(
                new FirstRequestRow(POST, "/users"),
                new FirstRequestRow(POST, "/users/1/posts"),
                new FirstRequestRow(POST, "/users/1/albums")
        ));
        requestRowMap.put("ROLE_POSTS", List.of(
                new FirstRequestRow(POST, "/posts"),
                new FirstRequestRow(PUT, "/posts/1"),
                new FirstRequestRow(DELETE, "/posts/1")
        ));
        requestRowMap.put("ROLE_ALBUMS", List.of(
                new FirstRequestRow(POST, "/albums"),
                new FirstRequestRow(PUT, "/albums/1"),
                new FirstRequestRow(DELETE, "/albums/1")
        ));
    }

    @Test
    void testAdminRole() throws Exception {
        Role admin = Role.ROLE_ADMIN;
        for (Map.Entry<String, List<FirstRequestRow>> pair : requestRowMap.entrySet()){
            List<FirstRequestRow> rows = pair.getValue();
            for (FirstRequestRow row: rows){
                if (!admin.checkRequestPossibility(row.method, row.path))
                    throw new Exception("ADMIN can't send: " + row.method + " " + row.path);
            }
        }
    }

    @Test
    void testCorrectAccessStateToEveryRequestForEveryRole() throws Exception {
        for (Role role : Role.values()) {
            for (Map.Entry<String, List<FirstRequestRow>> pair : requestRowMap.entrySet()) {
                Role smallestRoleHavingAccess = Role.valueOf(pair.getKey());
                List<FirstRequestRow> rows = pair.getValue();
                for (FirstRequestRow row : rows) {
                    boolean isIncludeNeededRole = role.includesRole(smallestRoleHavingAccess);
                    boolean isRequestAllowed = role.checkRequestPossibility(row.method, row.path);
                    if (isIncludeNeededRole != isRequestAllowed)
                        throw new Exception(role + " has undefined behavior on " + row.method + " " + row.path);
                }
            }
        }
    }


    static class FirstRequestRow {
        String method;
        String path;
        FirstRequestRow(String method, String path) {
            this.method = method;
            this.path = path;
        }
    }
}
