package com.shinchik.cloudkeeper.storage.controller;

import com.shinchik.cloudkeeper.storage.mapper.BreadcrumbMapper;
import com.shinchik.cloudkeeper.storage.model.BaseReqDto;
import com.shinchik.cloudkeeper.storage.model.BaseRespDto;
import com.shinchik.cloudkeeper.storage.model.Breadcrumb;
import com.shinchik.cloudkeeper.storage.service.MinioService;
import com.shinchik.cloudkeeper.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@Profile("web")
public class SearchController {

    private MinioService minioService;

    @Autowired
    public SearchController(MinioService minioService) {
        this.minioService = minioService;
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "query", required = false, defaultValue = "") String query,
                         @AuthenticationPrincipal(expression = "getUser") User user,
                         Model model) {

        query = query.trim().replaceAll("\\s+", " ");
        BaseReqDto searchReq = new BaseReqDto(user, "", query);
        List<BaseRespDto> foundObjects = minioService.search(searchReq);
        List<Breadcrumb> breadcrumbs = foundObjects.stream()
                .map(obj -> BreadcrumbMapper.INSTANCE.mapToModel(obj.getObjName()))
                .toList();
        List<BaseRespDto> objRefs = breadcrumbs.stream()
                .map(brcr -> new BaseRespDto(brcr.getLastPath(), brcr.getLastPart()))
                .toList();

        model.addAttribute("query", query);
        model.addAttribute("user", user);
        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("objRefs", objRefs);

        return "storage/search";

    }


}
