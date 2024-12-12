package com.shinchik.cloudkeeper.storage.model.dto;

import com.shinchik.cloudkeeper.user.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseRespDto implements ExtendedStorageDto {

    @NotNull(message = "User must be authenticated")
    private User user;
    @NotNull(message = "Path must not be null")
    private String path;
    @NotBlank(message = "Object name must be specified")
    private String objName;
    @NotNull(message = "Dir attribute must not be null")
    @Setter(AccessLevel.NONE)
    private boolean dir;

}