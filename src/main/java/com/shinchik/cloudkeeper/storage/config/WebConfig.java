package com.shinchik.cloudkeeper.storage.config;

import com.shinchik.cloudkeeper.storage.config.handlers.BaseRequestArgumentResolver;
import com.shinchik.cloudkeeper.storage.config.handlers.MkDirRequestArgumentResolver;
import com.shinchik.cloudkeeper.storage.config.handlers.RenameRequestArgumentResolver;
import com.shinchik.cloudkeeper.storage.config.handlers.UploadRequestArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@Profile("web")
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(
            List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new BaseRequestArgumentResolver());
        argumentResolvers.add(new RenameRequestArgumentResolver());
        argumentResolvers.add(new MkDirRequestArgumentResolver());
        argumentResolvers.add(new UploadRequestArgumentResolver());
    }
}
