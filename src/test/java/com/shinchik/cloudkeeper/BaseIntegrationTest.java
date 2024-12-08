package com.shinchik.cloudkeeper;

import com.shinchik.cloudkeeper.storage.MinioClientProperties;
import com.shinchik.cloudkeeper.storage.MinioConfig;
import com.shinchik.cloudkeeper.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


@Testcontainers
@SpringBootTest
@ActiveProfiles({"dev", "test"})
public class BaseIntegrationTest {

    @Autowired
    private Environment env;

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withUsername("test")
            .withPassword("testpass");

    @Container
    private static final MinIOContainer minio = new MinIOContainer("minio/minio:latest")
            .withUserName("test")
            .withPassword("testpass")
//            .withPassword("testpass")
            .withExposedPorts(9000, 9001);

    @Container
    private static final GenericContainer<?> redis = new GenericContainer<>("redis:latest")
            .withExposedPorts(6379)
            .withCommand("redis-server --requirepass testpass");


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MinioClientProperties minioClientProperties;

    @Autowired
    private MinioConfig minioConfig;


    @Test
    public void testContainerPostgres() {

        assertDoesNotThrow(() -> userRepository.findByUsername(""));
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
        registry.add("spring.datasource.password", () -> "testpass");
    }


}
