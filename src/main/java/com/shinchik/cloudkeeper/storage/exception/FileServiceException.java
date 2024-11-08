package com.shinchik.cloudkeeper.storage.exception;

public class FileServiceException extends RuntimeException{
    public FileServiceException(String message) {
        super(message);
    }

    public FileServiceException(Throwable cause) {
        super(cause);
    }
}
