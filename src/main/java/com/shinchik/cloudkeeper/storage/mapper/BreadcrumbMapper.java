package com.shinchik.cloudkeeper.storage.mapper;

import com.shinchik.cloudkeeper.storage.model.BaseReqDto;
import com.shinchik.cloudkeeper.storage.model.Breadcrumb;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Mapper
public interface BreadcrumbMapper {

    BreadcrumbMapper INSTANCE = Mappers.getMapper(BreadcrumbMapper.class);

    @Mapping(target = "pathItems", source = "path", qualifiedByName = "splitPath")
    @Mapping(target = "lastPart", source = "path", qualifiedByName = "getCurDir")
    Breadcrumb mapToModel(String path);

    @Named("splitPath")
    default LinkedHashMap<String, String> splitPath(String path){

        path = correctPath(path);

        String[] parts = path.split("/");
        LinkedHashMap<String, String> breadcrumb = new LinkedHashMap<>();
        String partName = "Home";
        StringBuilder fullPath = new StringBuilder();
        breadcrumb.put(partName, fullPath.toString());
        for (int i = 1; i < parts.length - 1; i++) {
            partName = parts[i];
            fullPath.append("/").append(partName);
            breadcrumb.put(partName, fullPath.toString());
        }


        return breadcrumb;
    }

    @Named("getCurDir")
    default String getCurDir(String path) {
        path = correctPath(path);
        String[] parts = path.split("/");
        if (parts.length > 0)
            return parts[parts.length - 1];
        else {
            return "";
        }
    }


    default String correctPath(String path){
        if (path.endsWith("/")){
            path = path.substring(0, path.lastIndexOf("/"));
        }
        if (!path.startsWith("/")){
            path = "/" + path;
        }

        // TODO: check if Minio would fix multiple slashes and I don't need to do it (depends on business logic)

        return path;
    }

}
