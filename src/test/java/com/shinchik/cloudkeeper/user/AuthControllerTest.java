package com.shinchik.cloudkeeper.user;

import com.shinchik.cloudkeeper.security.SecurityConfig;
import com.shinchik.cloudkeeper.storage.BaseIntegrationTest;
import com.shinchik.cloudkeeper.user.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"auth", "web", "test"})
@DisplayName("Testing mock authentication requests")
public class AuthControllerTest extends BaseAuthIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityConfig securityConfig;

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Testing registration requests")
    class RegistrationTest {
        @Test
        @Order(1)
        @WithAnonymousUser
        @DisplayName("GET registration page for anonymous user -> 200 ok")
        public void getRegistrationPage_withAnonymousUser() throws Exception {
            mockMvc.perform(get(securityConfig.getRegisterUrl()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("auth/registration"))
                    .andExpect(model().attributeDoesNotExist("errorMessage"));
        }

        @Test
        @Order(2)
        @WithUserDetails("user")
        @DisplayName("GET registration page for authenticated user -> 302 redirect to home page")
        public void getRegistrationPage_withAuthenticatedUser() throws Exception {
            mockMvc.perform(get(securityConfig.getRegisterUrl()))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        @Order(3)
        @WithAnonymousUser
        @DisplayName("Registering new user -> 302 redirect to login page and record added into database")
        public void registerUser() throws Exception {
            mockMvc.perform(post(securityConfig.getRegisterUrl())
                            .param("username", "test")
                            .param("password", "test")
                            .param("passwordConfirmation", "test")
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl(securityConfig.getLoginUrl()));

            Assertions.assertTrue(userService.findByUsername("test").isPresent());
        }

        @Test
        @Order(4)
        @WithAnonymousUser
        @DisplayName("Registering new user with existing username -> 200 ok and showing error message at registration page")
        public void registerUser_withExistingUsername() throws Exception {
            mockMvc.perform(post(securityConfig.getRegisterUrl())
                            .param("username", "user")
                            .param("password", "test")
                            .param("passwordConfirmation", "test")
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("auth/registration"))
                    .andExpect(model().attributeExists("errorMessage"));
        }

        @Test
        @Order(5)
        @WithAnonymousUser
        @DisplayName("Registering new user and passwords do not match -> 200 ok and showing error message at registration page")
        public void registerUser_withDifferentPasswords() throws Exception {
            mockMvc.perform(post(securityConfig.getRegisterUrl())
                            .param("username", "user")
                            .param("password", "test")
                            .param("passwordConfirmation", "tset")
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("auth/registration"))
                    .andExpect(model().attributeExists("errorMessage"));
        }


    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Testing log in requests")
    class LoginTest {
        @Test
        @Order(1)
        @WithAnonymousUser
        @DisplayName("GET login page for anonymous user -> 200 ok")
        public void getLoginPage_withAnonymousUser() throws Exception {
            mockMvc.perform(get(securityConfig.getLoginUrl()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("auth/login"));
        }

        @Test
        @Order(2)
        @WithUserDetails("user")
        @DisplayName("GET login page for authenticated user -> 302 redirect to home page")
        public void getLoginPage_withAuthenticatedUser() throws Exception {
            mockMvc.perform(get(securityConfig.getLoginUrl()))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }


        @Test
        @Order(3)
        @WithAnonymousUser
        @DisplayName("Login existing user -> 200 ok")
        public void loginUser() throws Exception {
            mockMvc.perform(post(securityConfig.getLoginUrl())
                            .param("username", "user")
                            .param("password", "1234")
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"));
        }

        @Test
        @Order(4)
        @WithAnonymousUser
        @DisplayName("Login non-existing user -> 302 redirect login page with error param")
        public void loginNonExistentUser() throws Exception {
            mockMvc.perform(post(securityConfig.getLoginUrl())
                            .param("username", "user1")
                            .param("password", "test")
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("/**/%s?error".formatted(securityConfig.getLoginUrl())));
        }

        @Test
        @Order(5)
        @WithAnonymousUser
        @DisplayName("Login existing user with wrong password -> 302 redirect login page with error param")
        public void loginUser_withInvalidCredentials() throws Exception {
            mockMvc.perform(post(securityConfig.getLoginUrl())
                            .param("username", "user")
                            .param("password", "test")
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("/**/%s?error".formatted(securityConfig.getLoginUrl())));
        }


    }


}
