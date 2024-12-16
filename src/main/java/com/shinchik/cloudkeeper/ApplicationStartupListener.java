package com.shinchik.cloudkeeper;

import com.shinchik.cloudkeeper.storage.repository.MinioRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "prod"})
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    private final MinioRepository minioRepository;

    @Autowired
    public ApplicationStartupListener(MinioRepository minioRepository) {
        this.minioRepository = minioRepository;
    }

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        minioRepository.createDefaultBucket();
    }
}
