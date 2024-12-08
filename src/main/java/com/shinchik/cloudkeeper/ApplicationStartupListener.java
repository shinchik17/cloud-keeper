package com.shinchik.cloudkeeper;

import com.shinchik.cloudkeeper.storage.service.BucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "prod"})
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    private final BucketService bucketService;

    @Autowired
    public ApplicationStartupListener(BucketService bucketService) {
        this.bucketService = bucketService;
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        bucketService.createDefaultBucket();
    }
}
