package com.shinchik.cloudkeeper.storage.dto;

import com.shinchik.cloudkeeper.user.model.User;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
public class RenameDto extends BaseReqDto {
    private String newObjName;

    public RenameDto(User user, String path, String objName, String newObjName) {
        super(user, path, objName);
        this.newObjName = newObjName;
    }

}
