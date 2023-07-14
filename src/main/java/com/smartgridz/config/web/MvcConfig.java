package com.smartgridz.config.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private UserInterceptor userInterceptor;

    private SessionTimerInterceptor sessionTimerInterceptor;

    private LicenseInterceptor licenseInterceptor;

    public MvcConfig(UserInterceptor userInterceptor,
                     SessionTimerInterceptor sessionTimerInterceptor,
                     LicenseInterceptor licenseInterceptor ) {
        this.userInterceptor = userInterceptor;
        this.sessionTimerInterceptor = sessionTimerInterceptor;
        this.licenseInterceptor = licenseInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor);
        registry.addInterceptor(sessionTimerInterceptor);
        registry.addInterceptor(licenseInterceptor);
    }
}
