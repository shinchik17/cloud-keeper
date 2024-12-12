package com.shinchik.cloudkeeper.storage.config.handlers;

import com.shinchik.cloudkeeper.storage.model.dto.UploadDto;
import jakarta.validation.Valid;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.util.List;


public class UploadRequestArgumentResolver extends BaseRequestArgumentResolver {

    private static final String MULTIPART_PARAM_NAME = "files";


    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterAnnotation(UploadRequest.class) != null;
    }


    @Override
    public Object resolveArgument(MethodParameter parameter,
                                     ModelAndViewContainer mavContainer,
                                     NativeWebRequest webRequest,
                                     WebDataBinderFactory binderFactory) throws Exception {

        UploadDto reqDto = new UploadDto(
                getUser(),
                getPath(webRequest),
                getFiles(webRequest)
        );

        if (parameter.hasParameterAnnotation(Valid.class)) {
            addBindingResult(reqDto, parameter.getParameterName(), webRequest, binderFactory, mavContainer);
        }

        return reqDto;

    }

    private static List<MultipartFile> getFiles(NativeWebRequest webRequest) {
        StandardMultipartHttpServletRequest request = (StandardMultipartHttpServletRequest) webRequest.getNativeRequest();
        return request.getFiles(MULTIPART_PARAM_NAME);
    }


}