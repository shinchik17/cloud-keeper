package com.shinchik.cloudkeeper.storage.exception;

public class NoSuchFolderException extends RuntimeException{
    public NoSuchFolderException(String message) {
        super(message);
    }

    public NoSuchFolderException(Throwable cause) {
        super(cause);
    }
}
