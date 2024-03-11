package com.intentionman.vkselectiontask.config;


import com.intentionman.vkselectiontask.security.RequestInterceptor;
import com.intentionman.vkselectiontask.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity(debug = true)
public class RequestInterceptorConfig implements WebMvcConfigurer {
    private final UserService userService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestInterceptor(userService));
    }
}