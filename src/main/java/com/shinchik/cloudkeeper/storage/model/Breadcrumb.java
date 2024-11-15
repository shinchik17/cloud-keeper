package com.shinchik.cloudkeeper.storage.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;


@NoArgsConstructor
@Getter
@Setter
public class Breadcrumb {

    LinkedHashMap<String, String> pathItems;
    String curDir;

    public Breadcrumb(LinkedHashMap<String, String> pathItems, String curDir) {
        this.pathItems = pathItems;
        this.curDir = curDir;
    }
}
