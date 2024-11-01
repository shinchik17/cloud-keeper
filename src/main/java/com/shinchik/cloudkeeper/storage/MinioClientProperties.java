package com.shinchik.cloudkeeper.storage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MinioClientProperties {
    private String endpoint;
    private String user;
    private String password;
}
