package com.shinchik.cloudkeeper.storage;

import com.shinchik.cloudkeeper.model.User;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileService {

    private final MinioClient minioClient;

    @Autowired
    public FileService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }


    public void uploadFile(User user){

    }



}
