package com.shinchik.cloudkeeper.storage.exception;

import com.shinchik.cloudkeeper.storage.util.PathUtils;

public class NoSuchObjectException extends RuntimeException{
    public NoSuchObjectException(String objPath) {
        super("File or folder '%s' does not exist".formatted(PathUtils.removeUserPrefix(objPath)));
    }

    public NoSuchObjectException(Throwable cause) {
        super(cause);
    }
}
