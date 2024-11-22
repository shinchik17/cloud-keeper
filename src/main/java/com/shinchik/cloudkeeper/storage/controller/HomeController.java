package com.shinchik.cloudkeeper.storage.controller;

import com.shinchik.cloudkeeper.storage.mapper.BreadcrumbMapper;
import com.shinchik.cloudkeeper.storage.model.*;
import com.shinchik.cloudkeeper.storage.service.MinioService;
import com.shinchik.cloudkeeper.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class HomeController {

    private MinioService minioService;

    @Autowired
    public HomeController(MinioService minioService) {
        this.minioService = minioService;
    }

    @GetMapping("/welcome")
    public String welcome(){
        return "welcome";
    }

    @GetMapping("/search")
    public ModelAndView search(@RequestParam(value = "query", required = false, defaultValue = "") String query,
                         @AuthenticationPrincipal(expression = "getUser") User user){

        query = query.trim().replaceAll("\\s+", " ");
        BaseReqDto searchReq = new BaseReqDto(user, "", query);
        List<BaseRespDto> foundObjects = minioService.search(searchReq);

        return new ModelAndView("storage/search", "foundObjects", foundObjects);

    }


    @GetMapping
    public String home(@RequestParam(value = "path", required = false, defaultValue = "") String path,
                       @AuthenticationPrincipal(expression = "getUser") User user,
                       Model model) {

        Breadcrumb breadcrumb = BreadcrumbMapper.INSTANCE.mapToModel(path);

        List<BaseRespDto> userObjects = minioService.list(new BaseReqDto(user, path));

        model.addAttribute("path", path);
        model.addAttribute("breadcrumb", breadcrumb);
        model.addAttribute("userObjects", userObjects);
        model.addAttribute("user", user);
        model.addAttribute("uploadDto", new UploadDto());
        model.addAttribute("downloadDto", new BaseRespDto());
        model.addAttribute("renameDto", new RenameDto());
        model.addAttribute("deleteDto", new BaseReqDto());

        return "html/home";
    }


}
