package com.shinchik.cloudkeeper.storage.model.dto;

import com.shinchik.cloudkeeper.user.model.User;


public interface StorageDto {

    User getUser();
    String getPath();

}
