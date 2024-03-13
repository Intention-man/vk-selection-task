package com.intentionman.vkselectiontask.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intentionman.vkselectiontask.TestDataUtil;
import com.intentionman.vkselectiontask.TestDataUtil.Post;
import com.intentionman.vkselectiontask.TestDataUtil.RequestData;
import com.intentionman.vkselectiontask.domain.dto.UserDto;
import com.intentionman.vkselectiontask.domain.entities.Role;
import com.intentionman.vkselectiontask.mappers.UserMapper;
import com.intentionman.vkselectiontask.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ProxyControllerIntegrationTest {
    private final AuthService authService;
    private final UserMapper userMapper;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    Map<String, List<RequestData>> requestMap = new HashMap<>();
    Map<Role, String> roleToTokenMap = new HashMap<>(); // role -> token
    private String adminToken;
    private String photographToken;
    private String postsReaderToken;
    final String GET = "GET";
    final String POST = "POST";
    final String PUT = "PUT";
    final String DELETE = "DELETE";


    @BeforeEach
    void fillRequestDataMap() {
        Post post = TestDataUtil.createTestPost();
        Post album = TestDataUtil.createTestPost();
//        requestMap.put("ROLE_DEFAULT", List.of(
//                new RequestData(GET, "/auth/registration"),
//                new RequestData(GET, "/auth/login"),
//                new RequestData(GET, "/v3/api-docs/")
//        ));
        requestMap.put("ROLE_USERS_VIEWER", List.of(new RequestData(GET, "/users")));
        requestMap.put("ROLE_POSTS_VIEWER", List.of(new RequestData(GET, "/posts")));
        requestMap.put("ROLE_ALBUMS_VIEWER", List.of(new RequestData(GET, "/albums")));
        requestMap.put("ROLE_USERS", List.of(
                new RequestData(POST, "/users"),
                new RequestData(POST, "/users/1/posts"),
                new RequestData(POST, "/users/1/albums")
        ));
        requestMap.put("ROLE_POSTS", List.of(
                new RequestData(POST, "/posts", post),
                new RequestData(PUT, "/posts/1", post),
                new RequestData(DELETE, "/posts/1")
        ));
        requestMap.put("ROLE_ALBUMS", List.of(
                new RequestData(POST, "/albums", album),
                new RequestData(PUT, "/albums/1", album),
                new RequestData(DELETE, "/albums/1")
        ));
    }

    @BeforeEach
    void getTokens() {
        UserDto admin = userMapper.entityToUserDto(TestDataUtil.createTestAdminRole());
        adminToken = "Bearer " + authService.signUp(admin).getToken();

        UserDto photograph = userMapper.entityToUserDto(TestDataUtil.createTestAlbumsRole());
        photographToken = "Bearer " + authService.signUp(photograph).getToken();

        UserDto postsReader = userMapper.entityToUserDto(TestDataUtil.createTestPostsReaderRole());
        postsReaderToken = "Bearer " + authService.signUp(postsReader).getToken();

        roleToTokenMap.put(Role.ROLE_ADMIN, adminToken);
        roleToTokenMap.put(Role.ROLE_ALBUMS, photographToken);
        roleToTokenMap.put(Role.ROLE_POSTS_VIEWER, postsReaderToken);
    }

    @Test
    void testCorrectEveryRequestStatusCodeForEveryRole() throws Exception {
        System.out.println(roleToTokenMap.size());
        System.out.println(requestMap.size());

        for (Role role : roleToTokenMap.keySet()) {
            String token = roleToTokenMap.get(role);
            for (Map.Entry<String, List<RequestData>> pair : requestMap.entrySet()) {
                Role smallestRoleHavingAccess = Role.valueOf(pair.getKey());
                List<RequestData> requests = pair.getValue();
                for (RequestData request : requests) {
                    boolean isIncludeNeededRole = role.includesRole(smallestRoleHavingAccess);
                    switch (request.method) {
                        case (GET):
                            executeGetRequest(request, token, isIncludeNeededRole, role);
                            break;
                        case (POST):
                            executePostRequest(request, token, isIncludeNeededRole, role);
                            break;
                        case (PUT):
                            executePutRequest(request, token, isIncludeNeededRole);
                            break;
                        case (DELETE):
                            executeDeleteRequest(request, token, isIncludeNeededRole);
                            break;
                    }
                }
            }
        }
    }

    public void executeGetRequest(RequestData request, String token, boolean isIncludeNeededRole, Role role) throws Exception {
        int expectedStatus = isIncludeNeededRole ? 200 : 403;
        mockMvc.perform(
                MockMvcRequestBuilders.get(request.path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
        ).andExpect(
                MockMvcResultMatchers.status().is(expectedStatus)
        );
    }

    public void executePostRequest(RequestData request, String token, boolean isIncludeNeededRole, Role role) throws Exception {
        String bodyJson = objectMapper.writeValueAsString(request.body);

        int expectedStatus = isIncludeNeededRole ? 200 : 403;
        mockMvc.perform(
                MockMvcRequestBuilders.post(request.path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(bodyJson)
        ).andExpect(
                MockMvcResultMatchers.status().is(expectedStatus)
        );
    }

    public void executePutRequest(RequestData request, String token, boolean isIncludeNeededRole) throws Exception {
        String bodyJson = objectMapper.writeValueAsString(request.body);

        int expectedStatus = isIncludeNeededRole ? 200 : 403;
        mockMvc.perform(
                MockMvcRequestBuilders.put(request.path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(bodyJson)
        ).andExpect(
                MockMvcResultMatchers.status().is(expectedStatus)
        );
    }

    public void executeDeleteRequest(RequestData request, String token, boolean isIncludeNeededRole) throws Exception {
        int expectedStatus = isIncludeNeededRole ? 204 : 403;
        mockMvc.perform(
                MockMvcRequestBuilders.delete(request.path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
        ).andExpect(
                MockMvcResultMatchers.status().is(expectedStatus)
        );
    }
}
