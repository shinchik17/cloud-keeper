package com.shinchik.cloudkeeper.storage.exception.service;

import com.shinchik.cloudkeeper.storage.util.PathUtils;

public class NoSuchObjectException extends MinioServiceException{
    public NoSuchObjectException(String objPath) {
        super("File or folder '%s' does not exist".formatted(PathUtils.removeUserPrefix(objPath)));
    }
}
