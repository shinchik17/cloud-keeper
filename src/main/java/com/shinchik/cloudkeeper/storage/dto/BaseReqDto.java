package com.shinchik.cloudkeeper.storage.dto;

import com.shinchik.cloudkeeper.user.model.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseReqDto {
    private User user;
    private String path;
    private String objName;

    public BaseReqDto(User user, String path) {
        this.user = user;
        this.path = path;
    }
}
