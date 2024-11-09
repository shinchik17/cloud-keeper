package com.shinchik.cloudkeeper.storage;

import com.shinchik.cloudkeeper.storage.repository.MinioRepository;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.unit.DataSize;

//@TestConfiguration
public class TestMinioConfig {

    private final String endpoint;
    private final String user;
    private final String password;
    private final String bucketName;
    private final int maxFindKeys;
    private final DataSize maxFileSize;
    private final DataSize maxRequestSize;

    public TestMinioConfig(Environment env) {
        try {
            this.endpoint = env.getRequiredProperty("minio.client.endpoint");
            this.user = env.getRequiredProperty("minio.client.user");
            this.password = env.getRequiredProperty("minio.client.password");
            this.bucketName = env.getRequiredProperty("minio.bucket-name");
            this.maxFindKeys = Integer.parseInt(env.getRequiredProperty("minio.max-listed-objects"));
            this.maxFileSize = DataSize.parse(env.getRequiredProperty("spring.servlet.multipart.max-file-size"));
            this.maxRequestSize = DataSize.parse(env.getRequiredProperty("spring.servlet.multipart.max-request-size"));
        } catch (IllegalStateException | NumberFormatException e) {
            throw new RuntimeException("Failed to get required property from spring context");
        }

    }

    @Bean
    public MinioClientProperties minioClientProperties() {
        return new MinioClientProperties(endpoint, user, password);
    }

    @Bean
    public MinioClient minioClient(MinioClientProperties minioClientProperties) {
        return MinioClient.builder()
                .endpoint(minioClientProperties.getEndpoint())
                .credentials(minioClientProperties.getUser(), minioClientProperties.getPassword())
                .build();

    }

    @Bean
    public MinioRepository minioRepository(MinioClient minioClient){
        return new MinioRepository(minioClient, bucketName, maxFindKeys);
    }

    @Bean
    public BucketService bucketService(MinioClient minioClient) throws MinioException {
        return new BucketService(minioClient, bucketName);
    }

    @Bean
    FileService fileService(MinioRepository minioRepository){
        return new FileService(minioRepository, maxFileSize, maxRequestSize);
    }


}
