package com.shinchik.cloudkeeper.storage.util;

import com.shinchik.cloudkeeper.storage.model.BaseReqDto;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@UtilityClass
public class PathUtils {

    public static String normalize(@NotNull String path) {

        path = path.trim().replaceAll("\\s+", " ");
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        try {
            return URI.create(path).normalize().toString();
        } catch (IllegalArgumentException | NullPointerException e) {
            log.info("Caught exception when trying to normalize path '{}' as uri: {}", path, e.getMessage());
            return path;
        }

    }

    public static String encode(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    public static String getEncodedPath(BaseReqDto reqDto) {
        return URLEncoder.encode(reqDto.getPath(), StandardCharsets.UTF_8);
    }


}
