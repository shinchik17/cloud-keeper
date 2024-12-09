package com.shinchik.cloudkeeper.user;

import com.shinchik.cloudkeeper.user.exception.InvalidUserCredentialsException;
import com.shinchik.cloudkeeper.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO: devide integration and mockMvc tests

@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"auth", "test"})
public class AuthControllerTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withUsername("test")
            .withPassword("testpass");

    @Container
    private static final GenericContainer<?> redis = new GenericContainer<>("redis:latest")
            .withExposedPorts(6379)
            .withCommand("redis-server --requirepass testpass");


    @Value("${server.servlet.context-path}")
    private String serverContextPath;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Test
    @WithAnonymousUser
    public void testAccessRegisterPage_whenAnonymous() throws Exception {
        mockMvc.perform(get("/auth/register"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user")
    public void testAccessRegisterPage_whenAuthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/register"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user2")
    public void registerUser() throws Exception {
        mockMvc.perform(get("/auth/register"));
        Assertions.assertTrue(userService.findByUsername("user2").isPresent());
    }

//    @Test
//    @WithMockUser(username = "user")
//    public void registerExistingUser_then() {

//        Assertions.assertThrows(InvalidUserCredentialsException.class,
//                () -> mockMvc.perform(post("/auth/register")));
//    }


    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.name", postgres::getDatabaseName);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        registry.add("spring.data.redis.password", () -> "testpass");
    }
}
