package com.shinchik.cloudkeeper.storage.model.dto;

import com.shinchik.cloudkeeper.user.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MkDirDto implements ExtendedStorageDto {

    @NotNull(message = "User must be authenticated")
    private User user;
    @NotNull(message = "Path must not be null")
    private String path;
    @NotBlank(message = "Object name must be specified")
    private String objName;

}
