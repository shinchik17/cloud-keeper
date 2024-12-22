package com.shinchik.cloudkeeper.storage.mapper;

import com.shinchik.cloudkeeper.storage.model.Breadcrumb;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.LinkedHashMap;

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
            if (i != 1){
                fullPath.append("/");
            }
            fullPath.append(partName);
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

        return path;
    }

}
