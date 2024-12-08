package com.shinchik.cloudkeeper.storage;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;

@Configuration
//@PropertySource("classpath:application.yaml")
public class MinioConfig {


    @Bean
    @ConfigurationProperties(prefix = "minio.client")
    public MinioClientProperties minioClientProperties(){
        return new MinioClientProperties();
    }

    @Bean
    public MinioClient minioClient(MinioClientProperties minioClientProperties){
        return MinioClient.builder()
                .endpoint(minioClientProperties.getEndpoint())
                .credentials(minioClientProperties.getUser(), minioClientProperties.getPassword())
                .build();
    }




}
