package com.intentionman.vkselectiontask.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intentionman.vkselectiontask.TestDataUtil;
import com.intentionman.vkselectiontask.domain.entities.UserEntity;
import com.intentionman.vkselectiontask.services.UserService;
import lombok.RequiredArgsConstructor;
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

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AuthControllerIntegrationTest {
    private final String REGISTRATION_PATH = "/auth/registration";
    private final String LOGIN_PATH = "/auth/login";
    private final UserService userService;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Test
    void testSuccessfullyRegistered() throws Exception {
        UserEntity testUser = TestDataUtil.createTestDefaultRole();
        String userJson = objectMapper.writeValueAsString(testUser);
        mockMvc.perform(
                MockMvcRequestBuilders.post(REGISTRATION_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        );
    }

    @Test
    void testTooShortRegistrationData() throws Exception {
        UserEntity shortUser = TestDataUtil.createTooShortUser();
        String userJson = objectMapper.writeValueAsString(shortUser);
        mockMvc.perform(
                MockMvcRequestBuilders.post(REGISTRATION_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    void testLoginIsOccupied() throws Exception {
        UserEntity testUser = TestDataUtil.createTestDefaultRole();
        String userJson = objectMapper.writeValueAsString(testUser);
        userService.save(testUser);

        mockMvc.perform(
                MockMvcRequestBuilders.post(REGISTRATION_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andExpect(
                MockMvcResultMatchers.status().isConflict()
        );
    }

    @Test
    void testCorrectLoginResponses() throws Exception {
        UserEntity testUser = TestDataUtil.createTestDefaultRole();
        String userJson = objectMapper.writeValueAsString(testUser);
        userService.save(testUser);

        // OK-test
        mockMvc.perform(
                MockMvcRequestBuilders.post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.accessToken").isString()
        );

        // FORBIDDEN-test
        testUser = TestDataUtil.createTestDefaultRole();
        testUser.setPassword("incorrect-password");
        userJson = objectMapper.writeValueAsString(testUser);
        mockMvc.perform(
                MockMvcRequestBuilders.post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );

        // NOT_FOUND-test
        testUser = TestDataUtil.createTestDefaultRole();
        testUser.setUsername("not-existing-username");
        userJson = objectMapper.writeValueAsString(testUser);
        mockMvc.perform(
                MockMvcRequestBuilders.post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }
}