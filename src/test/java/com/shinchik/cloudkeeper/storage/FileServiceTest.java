package com.shinchik.cloudkeeper.storage;

import com.shinchik.cloudkeeper.model.User;
import com.shinchik.cloudkeeper.storage.dto.file.FileCheckDto;
import com.shinchik.cloudkeeper.storage.dto.file.FileUploadDto;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestMinioConfig.class, initializers = ConfigDataApplicationContextInitializer.class)
class FileServiceTest {

    private static final String MINIO_IMAGE_NAME = "quay.io/minio/minio:latest";
    private static final DockerImageName MINIO_IMAGE = DockerImageName.parse("quay.io/minio/minio:latest");

//    @Value("${minio.client.user}")
//    private String minioUser;
//
//    @Value("${minio.client.password}")
//    private String minioPassword;


    @Container
//    private GenericContainer<?> minio = new GenericContainer<>(MINIO_IMAGE)
    private GenericContainer<?> minio = new FixedHostPortGenericContainer<>(MINIO_IMAGE_NAME)
            .withFixedExposedPort(9000, 9000)
            .withFixedExposedPort(9001, 9001)
//            .withExposedPorts(9000, 9001)
            .withCommand("server", "/data", "--console-address", ":9001")
            .withEnv("MINIO_ROOT_USER", "test")
            .withEnv("MINIO_ROOT_PASSWORD", "testpass")
            ;



    @Autowired
    private MinioClient minioClient;

    @Autowired
    private BucketService bucketService;

    @Autowired
    private FileService fileService;




    private static MockMultipartFile file1 = new MockMultipartFile("mockFile1.txt", new byte[]{1, 2, 3});
    private static MockMultipartFile file2 = new MockMultipartFile("mockFile2.txt", new byte[]{3, 2, 3});

    private User user = new User(1L, "user1", "pass1", "USER");
    private FileUploadDto uploadDto = new FileUploadDto(user, "folder/", List.of(file1));
    private FileCheckDto checkDto = new FileCheckDto(user, "folder/", file1.getName());

    @BeforeAll
    public static void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field origName = file1.getClass().getDeclaredField("originalFilename");
        origName.setAccessible(true);
        origName.set(file1, file1.getName());
        Field origName2 = file2.getClass().getDeclaredField("originalFilename");
        origName2.setAccessible(true);
        origName2.set(file2, file2.getName());
    }


    @Test
    void upload() {
        fileService.upload(uploadDto);
        assertTrue(fileService.isObjectExist(checkDto));
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
}