package com.intentionman.vkselectiontask.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intentionman.vkselectiontask.TestDataUtil;
import com.intentionman.vkselectiontask.domain.dto.UserDto;
import com.intentionman.vkselectiontask.domain.entities.UserEntity;
import com.intentionman.vkselectiontask.mappers.UserMapper;
import com.intentionman.vkselectiontask.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
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
public class ProxyControllerIntegrationTest {
    private final AuthService authService;
    private final UserMapper userMapper;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private String token;

    @BeforeAll
    void setUp() {
        UserDto testUser = userMapper.entityToUserDto(TestDataUtil.createTestDefaultRole());
        token = "Bearer " + authService.signUp(testUser).getToken();
    }

//    @Test
//    void testThatListPostsReturnsHttpStatus200() throws Exception {
//        UserEntity testUser = TestDataUtil.createTestUser();
//        PointEntity testPoint = TestDataUtil.createTestPointEntity(testUser);
//        pointService.save(testPoint);
//        String pointJson = objectMapper.writeValueAsString(testUser.getUserId());
//        mockMvc.perform(
//                MockMvcRequestBuilders.get("/posts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("Authorization", token)
//                        .content(pointJson)
//        ).andExpect(
//                MockMvcResultMatchers.status().isOk()
//        );
//    }
//
//    @Test
//    void testThatCreatePointSuccessfullyReturnsHttp201Created() throws Exception {
//        UserEntity testUser = TestDataUtil.createTestUser();
//
//        PointEntity testPoint = TestDataUtil.createTestPointEntity(testUser);
//        testPoint.setPointId(null);
//        String pointJson = objectMapper.writeValueAsString(testPoint);
//
//        mockMvc.perform(
//                MockMvcRequestBuilders.post("/points")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("Authorization", token)
//                        .content(pointJson)
//        ).andExpect(
//                MockMvcResultMatchers.status().isCreated()
//        );
//    }
}
