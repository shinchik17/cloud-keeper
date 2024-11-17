package com.shinchik.cloudkeeper.storage.model;

import com.shinchik.cloudkeeper.user.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

// TODO: validation patterns on fields
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseRespDto {
//    @NotNull(message = "User must be authorized to use service")
    private User user;
    @NotNull(message = "Path should not be null")
    private String path;
    @NotBlank(message = "Object name must be specified")
    private String objName;
    @NotNull
    @Setter(AccessLevel.NONE)
    private boolean dir;

}
