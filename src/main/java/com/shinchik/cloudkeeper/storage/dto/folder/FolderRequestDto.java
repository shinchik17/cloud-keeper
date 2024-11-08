package com.shinchik.cloudkeeper.storage.dto.folder;

import com.shinchik.cloudkeeper.model.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class FolderRequestDto {
    private User user;
}
