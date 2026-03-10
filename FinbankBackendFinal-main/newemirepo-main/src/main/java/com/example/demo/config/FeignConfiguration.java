package com.example.demo.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {

            @Override
            public void apply(RequestTemplate template) {

                ServletRequestAttributes attrs =
                        (ServletRequestAttributes)
                                RequestContextHolder.getRequestAttributes();

                if (attrs != null) {
                    HttpServletRequest request =
                            attrs.getRequest();

                    String token =
                            request.getHeader("Authorization");

                    if (token != null) {
                        template.header("Authorization", token);
                    }
                }
            }
        };
    }
}