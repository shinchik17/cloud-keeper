package com.shinchik.cloudkeeper.storage;

import com.shinchik.cloudkeeper.storage.model.dto.BaseReqDto;
import com.shinchik.cloudkeeper.storage.service.MinioService;
import com.shinchik.cloudkeeper.user.model.Role;
import com.shinchik.cloudkeeper.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"dev", "test"})
public class WebMvcIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withUsername("test")
            .withPassword("testpass");

    @Container
    private static final MinIOContainer minio = new MinIOContainer("minio/minio:latest")
            .withUserName("test")
            .withPassword("testpass")
            .withExposedPorts(9000, 9001);

    @Container
    private static final GenericContainer<?> redis = new GenericContainer<>("redis:latest")
            .withExposedPorts(6379)
            .withCommand("redis-server --requirepass testpass");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MinioService minioService;


    @Autowired
    private RedisProperties redisProperties;


    @Test
    @WithUserDetails("user")
    public void getHomePage_withAuthenticatedUser_thenReturnViewName() throws Exception {
        mockMvc.perform(get("/")).andDo(print())
                .andExpect(view().name("storage/home"));
    }

    @Test
    @WithAnonymousUser
    public void getHomePage_withAnonymousUser_thenRedirect() throws Exception {
        mockMvc.perform(get("/")).andDo(print())
                .andExpect(status().is3xxRedirection());
    }


    @Test
    @WithUserDetails("user")
    public void uploadFiles_thenReloadHomePage() throws Exception {
//        MockHttpServletRequestBuilder request = post("/files")
//                .contentType(MediaType.MULTIPART_FORM_DATA)
//                .
//                .param("files", generateMockMultipartFiles(1))

        MockMultipartFile mockFile = generateMockMultipartFiles(1).get(0);
        mockMvc.perform(multipart("/files").file("documents", mockFile.getBytes()).with(csrf()))
                .andExpect(status().is3xxRedirection());

        User user = new User(1L, "user", "1234", Role.USER);
        assertTrue(minioService.isObjectExist(new BaseReqDto(user, "", mockFile.getOriginalFilename())));


    }


//    private static List<MultipartFile> generateMockMultipartFilesAndFolder(int numObjToUpload) {
//        List<MultipartFile> files = generateMockMultipartFiles(numObjToUpload - 1);
//        files.add(createMockMultipartFolder());
//        return files;
//    }

    private static List<MockMultipartFile> generateMockMultipartFiles(int amount) {
        ArrayList<MockMultipartFile> multipartFiles = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            multipartFiles.add(
                    new MockMultipartFile(
                            "name",
                            "MockName" + i + ".txt",
                            "plain/text",
                            new byte[]{1, 2, 3})
            );
        }

        return multipartFiles;

    }

    private static MultipartFile createMockMultipartFolder() {
        return new MockMultipartFile(
                "fakeFolder/",
                "fakeFolder/",
                "content",
                new byte[]{});
    }


    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.name", postgres::getDatabaseName);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @DynamicPropertySource
    static void minioProperties(DynamicPropertyRegistry registry) {
        registry.add("minio.client.endpoint", () -> "http:/localhost:" + minio.getMappedPort(9000));
        registry.add("minio.client.user", () -> "test");
        registry.add("minio.client.password", () -> "testpass");
    }

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        registry.add("spring.data.redis.password", () -> "testpass");
    }

}
