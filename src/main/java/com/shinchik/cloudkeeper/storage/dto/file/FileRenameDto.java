package com.shinchik.cloudkeeper.storage.dto.file;

import com.shinchik.cloudkeeper.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileRenameDto extends FileRequestDto{
    private String objName;
    private String newObjName;

    public FileRenameDto(User user, String path, String objName, String newObjName) {
        super(user, path);
        this.objName = objName;
        this.newObjName = newObjName;
    }

}
