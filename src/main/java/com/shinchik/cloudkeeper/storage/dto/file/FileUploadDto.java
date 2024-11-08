package com.shinchik.cloudkeeper.storage.dto.file;

import com.shinchik.cloudkeeper.model.User;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FileUploadDto extends FileRequestDto{
    private List<MultipartFile> documents;

    public FileUploadDto(User user, String path, List<MultipartFile> documents) {
        super(user, path);
        this.documents = documents;
    }

}
