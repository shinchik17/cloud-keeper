package com.shinchik.cloudkeeper.storage.exception.service;

public class MinioServiceException extends RuntimeException{
    public MinioServiceException(String message) {
        super(message);
    }

    public MinioServiceException(Throwable cause) {
        super(cause);
    }
}
