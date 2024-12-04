package com.shinchik.cloudkeeper.storage.exception.service;

public class NotEnoughFreeSpaceException extends MinioServiceException{
    public NotEnoughFreeSpaceException(long spaceSize) {
        super("You have run out of free space size (%d MB). Consider purchasing a subscription to get more space".formatted(spaceSize));
    }
}
