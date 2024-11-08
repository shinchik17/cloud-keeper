package com.shinchik.cloudkeeper.storage.exception;

public class MinioRepositoryException extends RuntimeException{
    public MinioRepositoryException(String message) {
        super(message);
    }

    public MinioRepositoryException(Throwable cause) {
        super(cause);
    }
}
