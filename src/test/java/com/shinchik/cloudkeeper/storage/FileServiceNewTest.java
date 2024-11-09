package com.shinchik.cloudkeeper.storage;

import com.shinchik.cloudkeeper.model.User;
import com.shinchik.cloudkeeper.storage.dto.file.FileCheckDto;
import com.shinchik.cloudkeeper.storage.dto.file.FileUploadDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@Import(TestConfigMock.class)
class FileServiceNewTest {

    private static final String MINIO_IMAGE_NAME = "quay.io/minio/minio:latest";
    private static final DockerImageName MINIO_IMAGE = DockerImageName.parse("quay.io/minio/minio:latest");

    // TODO: возможно, лучше всё-таки сделать рандомные порты. Есть вероятность, что из-за них тесты иногда валятся в IOException
    // @Container
    private GenericContainer<?> minioContainer = new FixedHostPortGenericContainer<>(MINIO_IMAGE_NAME)
            .withFixedExposedPort(9000, 9000)
            .withFixedExposedPort(9001, 9001)
            .withCommand("server", "/data", "--console-address", ":9001");


    @Autowired
    private MinioClientProperties minioClientProperties;

    @Autowired
    private BucketService bucketService;

    @Autowired
    private FileService fileService;

    private List<MultipartFile> files = generateMockMultipartFiles(1);

    private User user = new User(1L, "user1", "pass1", "USER");
    private FileUploadDto uploadDto = new FileUploadDto(user, "folder/", files);
    private FileCheckDto checkDto = new FileCheckDto(user, "folder/", files.get(0).getOriginalFilename());


    @BeforeEach
    public void before() throws InterruptedException {
        minioContainer.withEnv("MINIO_ROOT_USER", minioClientProperties.getUser())
                .withEnv("MINIO_ROOT_PASSWORD", minioClientProperties.getPassword());
        minioContainer.start();
        Thread.sleep(2000); // needs for container to really get ready
        bucketService.createDefaultBucket();
    }

    @AfterEach
    public void after() {
        minioContainer.stop();
    }


//    @DynamicPropertySource
//    void minioProperties(DynamicPropertyRegistry registry) {
//        registry.add("minio.client.endpoint", () -> "http://localhost:" + minio.getMappedPort(9000));
//        registry.add("minio.client.user", () -> "user");
//        registry.add("minio.client.password", () -> "minio_password");
//        registry.add("minio.bucket-name", () -> "user-files");
//    }

    @Test
    void upload() {
        fileService.upload(uploadDto);
        assertTrue(fileService.isObjectExist(checkDto));
        System.out.println();
    }

    @Test
    void download() {
    }

    @Test
    void rename() {
    }

    @Test
    void delete() {
    }

    private static List<MultipartFile> generateMockMultipartFiles(int amount) {
        ArrayList<MultipartFile> multipartFiles = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            multipartFiles.add(
                    new MockMultipartFile(
                            "name",
                            "MockName" + i,
                            "plain/text",
                            new byte[]{})
            );
        }

        return multipartFiles;

    }


}