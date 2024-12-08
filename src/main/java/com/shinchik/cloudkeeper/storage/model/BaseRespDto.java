package com.shinchik.cloudkeeper.storage.model;

import com.shinchik.cloudkeeper.user.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseRespDto {

    private User user;
    @NotNull(message = "Path should not be null")
    private String path;
    @NotBlank(message = "Object name must be specified")
    private String objName;
    @NotNull()
    @Setter(AccessLevel.NONE)
    private boolean dir;

    public BaseRespDto(String path, String objName) {
        this.path = path;
        this.objName = objName;
    }
}
