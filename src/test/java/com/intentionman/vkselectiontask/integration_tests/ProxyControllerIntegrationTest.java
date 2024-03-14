package com.intentionman.vkselectiontask.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intentionman.vkselectiontask.TestDataUtil;
import com.intentionman.vkselectiontask.TestDataUtil.Album;
import com.intentionman.vkselectiontask.TestDataUtil.Post;
import com.intentionman.vkselectiontask.TestDataUtil.RequestData;
import com.intentionman.vkselectiontask.domain.dto.UserDto;
import com.intentionman.vkselectiontask.domain.entities.Role;
import com.intentionman.vkselectiontask.domain.entities.UserEntity;
import com.intentionman.vkselectiontask.mappers.UserMapper;
import com.intentionman.vkselectiontask.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
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
@EnableCaching
class ProxyControllerIntegrationTest {
    private final AuthService authService;
    private final UserMapper userMapper;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    Map<String, List<RequestData>> requestMap = new HashMap<>();
    Map<Role, String> roleToTokenMap = new HashMap<>(); // role -> token

    final String GET = "GET";
    final String POST = "POST";
    final String PUT = "PUT";
    final String DELETE = "DELETE";


    @BeforeEach
    void fillRequestDataMap() {
        Post post = TestDataUtil.createTestPost();
        Album album = TestDataUtil.createTestAlbum();
//        requestMap.put("ROLE_DEFAULT", List.of(
//                new RequestData(GET, "/v3/api-docs/")
//        ));
        requestMap.put("ROLE_POSTS_VIEWER", List.of(new RequestData(GET, "/posts")));
        requestMap.put("ROLE_ALBUMS_VIEWER", List.of(new RequestData(GET, "/albums")));
        requestMap.put("ROLE_USERS_VIEWER", List.of(
                new RequestData(GET, "/users"),
                new RequestData(GET, "/users/1"),
                new RequestData(GET, "/users/1/posts"),
                new RequestData(GET, "/users/1/albums")
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
        Map<Role, UserEntity> roleToTestUserMap = TestDataUtil.createTestUsers();
        for (Role role: roleToTestUserMap.keySet()){
            UserDto user = userMapper.entityToUserDto(roleToTestUserMap.get(role));
            String token = "Bearer " + authService.signUp(user).getToken();
            roleToTokenMap.put(role, token);
        }
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
