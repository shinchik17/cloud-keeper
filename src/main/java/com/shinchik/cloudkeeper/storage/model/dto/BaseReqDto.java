package com.shinchik.cloudkeeper.storage.model.dto;

import com.shinchik.cloudkeeper.user.model.User;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseReqDto implements ExtendedStorageDto {
    @NotNull(message = "User must be authenticated")
    private User user;
    @NotNull(message = "Path must not be null")
    private String path;
    @NotNull(message = "Object name must not be null")
    private String objName = "";

    public BaseReqDto(User user, String path) {
        this.user = user;
        this.path = path;
    }
}
