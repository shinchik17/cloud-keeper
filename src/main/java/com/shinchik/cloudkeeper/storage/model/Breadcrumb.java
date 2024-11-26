package com.shinchik.cloudkeeper.storage.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;
import java.util.LinkedHashMap;


@NoArgsConstructor
@Getter
@Setter
public class Breadcrumb {

    private LinkedHashMap<String, String> pathItems;
    private String curDir;

    public Breadcrumb(LinkedHashMap<String, String> pathItems, String curDir) {
        this.pathItems = pathItems;
        this.curDir = curDir;
    }

    public String getLastPath(){
        return pathItems.values().stream().max(Comparator.comparingInt(String::length)).orElse("");
    }


}
