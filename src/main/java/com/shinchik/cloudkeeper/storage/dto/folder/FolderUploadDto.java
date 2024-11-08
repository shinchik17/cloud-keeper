package com.shinchik.cloudkeeper.storage.dto.folder;

import com.shinchik.cloudkeeper.model.User;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FolderUploadDto extends FolderRequestDto {
    private List<MultipartFile> documents;

    public FolderUploadDto(User user, List<MultipartFile> documents) {
        super(user);
        this.documents = documents;
    }
}
