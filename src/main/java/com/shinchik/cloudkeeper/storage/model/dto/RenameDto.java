package com.shinchik.cloudkeeper.storage.model.dto;

import com.shinchik.cloudkeeper.user.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RenameDto implements ExtendedStorageDto {

    @NotNull(message = "User must be authenticated")
    private User user;
    @NotNull(message = "Path must not be null")
    private String path;
    @NotBlank(message = "Object to rename must not be null")
    private String objName;
    @NotBlank(message = "New object name must not be empty")
    private String newObjName;

}