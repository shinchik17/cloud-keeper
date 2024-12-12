package com.shinchik.cloudkeeper.storage.model.dto;

import com.shinchik.cloudkeeper.user.model.User;


public interface ExtendedStorageDto extends StorageDto {

    User getUser();
    String getPath();

    String getObjName();

}
