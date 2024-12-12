package com.shinchik.cloudkeeper.storage.model;

import com.shinchik.cloudkeeper.user.model.User;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UploadDto extends BaseReqDto {
    @NotEmpty(message = "You should select one or more files to upload")
    private List<MultipartFile> files;

    public UploadDto(User user, String path, List<MultipartFile> files) {
        super(user, path, "");
        this.files = files;
    }

}
