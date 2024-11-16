package com.shinchik.cloudkeeper.storage.model;

import com.shinchik.cloudkeeper.user.model.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// TODO: validation patterns on fields
@Getter
@Setter
@NoArgsConstructor
public class BaseRespDto extends BaseReqDto {
    @NotBlank(message = "Object must be specified")
    private String objName;
    private boolean isObjDir;

    public BaseRespDto(User user, String path, String objName, boolean isObjDir) {
        super(user, path);
        this.objName = objName;
        this.isObjDir = isObjDir;
    }

}
