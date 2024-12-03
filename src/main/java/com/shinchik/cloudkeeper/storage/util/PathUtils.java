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


    public static String formFullPath(BaseReqDto reqDto) {
        return "user-%d-files/%s/".formatted(reqDto.getUser().getId(), reqDto.getPath()).replace("//", "/");
    }

    public static String removeUserPrefix(String prefixedPath) {
        return prefixedPath.replaceFirst("user-[0-9]{1,18}-files/", "");
    }

    public static String extractOrigName(String fullObjPath) {
        return fullObjPath.substring(fullObjPath.lastIndexOf("/") + 1);
    }

    public static String extractNameFromPath(String fullObjPath, String path) {
        fullObjPath = fullObjPath.substring(fullObjPath.lastIndexOf(path));
        return removeUserPrefix(fullObjPath);
    }

    public static String handleFileExtension(String oldName, String newName) {
        int lastDotIndex = oldName.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return newName + oldName.substring(lastDotIndex);
        }

        return newName;
    }
}
