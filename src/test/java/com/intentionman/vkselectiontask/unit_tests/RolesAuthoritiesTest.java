package com.intentionman.vkselectiontask.unit_tests;

import com.intentionman.vkselectiontask.TestDataUtil.RequestData;
import com.intentionman.vkselectiontask.domain.entities.Role;
import lombok.RequiredArgsConstructor;
import org.junit.BeforeClass;
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
    Map<String, List<RequestData>> requestRowMap = new HashMap<>();

    @BeforeEach
    void setUp() {
        String GET = "GET";
        String POST = "POST";
        String PUT = "PUT";
        String DELETE = "DELETE";
        requestRowMap.put("ROLE_DEFAULT", List.of(
                new RequestData(GET, "/auth/registration"),
                new RequestData(GET, "/auth/login"),
                new RequestData(GET, "/v3/api-docs/")
        ));
        requestRowMap.put("ROLE_USERS_VIEWER", List.of(new RequestData(GET, "/users")));
        requestRowMap.put("ROLE_POSTS_VIEWER", List.of(new RequestData(GET, "/posts")));
        requestRowMap.put("ROLE_ALBUMS_VIEWER", List.of(new RequestData(GET, "/albums")));
        requestRowMap.put("ROLE_USERS", List.of(
                new RequestData(POST, "/users"),
                new RequestData(POST, "/users/1/posts"),
                new RequestData(POST, "/users/1/albums")
        ));
        requestRowMap.put("ROLE_POSTS", List.of(
                new RequestData(POST, "/posts"),
                new RequestData(PUT, "/posts/1"),
                new RequestData(DELETE, "/posts/1")
        ));
        requestRowMap.put("ROLE_ALBUMS", List.of(
                new RequestData(POST, "/albums"),
                new RequestData(PUT, "/albums/1"),
                new RequestData(DELETE, "/albums/1")
        ));
    }

    @Test
    void testAdminRole() throws Exception {
        Role admin = Role.ROLE_ADMIN;
        for (Map.Entry<String, List<RequestData>> pair : requestRowMap.entrySet()){
            List<RequestData> rows = pair.getValue();
            for (RequestData row: rows){
                if (!admin.checkRequestPossibility(row.method, row.path))
                    throw new Exception("ADMIN can't send: " + row.method + " " + row.path);
            }
        }
    }

    @Test
    void testCorrectAccessStateToEveryRequestForEveryRole() throws Exception {
        System.out.println(requestRowMap.size());

        for (Role role : Role.values()) {
            for (Map.Entry<String, List<RequestData>> pair : requestRowMap.entrySet()) {
                Role smallestRoleHavingAccess = Role.valueOf(pair.getKey());
                List<RequestData> rows = pair.getValue();
                for (RequestData row : rows) {
                    boolean isIncludeNeededRole = role.includesRole(smallestRoleHavingAccess);
                    boolean isRequestAllowed = role.checkRequestPossibility(row.method, row.path);
                    if (isIncludeNeededRole != isRequestAllowed)
                        throw new Exception(role + " has undefined behavior on " + row.method + " " + row.path);
                }
            }
        }
    }
}
