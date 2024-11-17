package com.shinchik.cloudkeeper.storage.model;

import com.shinchik.cloudkeeper.user.model.User;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

// TODO: validation patterns on fields
@Getter
@Setter
@NoArgsConstructor
public class RenameDto extends BaseReqDto {
    @NotBlank(message = "New name must not be empty")
    private String newObjName;

    public RenameDto(User user, String path,
                     @NotBlank(message = "You must specify object to rename") String objName,
                     String newObjName) {
        super(user, path, objName);
        this.newObjName = newObjName;
    }

}
