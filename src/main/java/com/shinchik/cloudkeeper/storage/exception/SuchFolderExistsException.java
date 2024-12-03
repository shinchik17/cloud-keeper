package com.shinchik.cloudkeeper.storage.exception;

public class SuchFolderExistsException extends RuntimeException{
    public SuchFolderExistsException(String message) {
        super(message);
    }

    public SuchFolderExistsException(Throwable cause) {
        super(cause);
    }
}
