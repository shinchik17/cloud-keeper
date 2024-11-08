package com.shinchik.cloudkeeper.storage.dto.file;

import com.shinchik.cloudkeeper.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class FileRequestDto {
    private User user;
    private String path;
}
