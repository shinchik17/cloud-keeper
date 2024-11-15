package com.shinchik.cloudkeeper.storage.model;

import com.shinchik.cloudkeeper.user.model.User;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseReqDto {

    @NotNull(message = "User must be authorized to use service")
    private User user;
    private String path;
    private String objName;

    public BaseReqDto(User user, String path) {
        this.user = user;
        this.path = path;
    }
}
