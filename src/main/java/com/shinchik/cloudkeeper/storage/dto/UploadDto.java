package com.shinchik.cloudkeeper.storage.dto;

import com.shinchik.cloudkeeper.model.User;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UploadDto extends BaseReqDto {
    private List<MultipartFile> documents;

    public UploadDto(User user, String path, List<MultipartFile> documents) {
        super(user, path);
        this.documents = documents;
    }

}
