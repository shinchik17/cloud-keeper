package com.shinchik.cloudkeeper.storage.util;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

@Slf4j
@UtilityClass
public class PathParser {

    public static String normalizePath(@NotNull String path){

        path = path.trim().replaceAll("\\s+", " ");
        if (path.startsWith("/")){
            path = path.substring(1);
        }

        try {
            return URI.create(path).normalize().toString();
        } catch (IllegalArgumentException | NullPointerException e) {
            log.info("Caught exception when trying to normalize path '%s' as uri. Caught: %s".formatted(path, e.getMessage()));
            return path;
        }

    }



}
