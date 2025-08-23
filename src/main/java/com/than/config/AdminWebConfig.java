package com.than.config;

import com.than.interceptor.VerifyInterceptor;
import io.micrometer.common.lang.NonNull;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AdminWebConfig implements WebMvcConfigurer {

    @Resource
    private VerifyInterceptor verifyInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(verifyInterceptor)
                .addPathPatterns("/*");
    }
}
