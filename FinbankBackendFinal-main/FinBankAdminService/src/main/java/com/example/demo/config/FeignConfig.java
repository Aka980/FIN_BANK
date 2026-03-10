package com.example.demo.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {

            ServletRequestAttributes attrs =
                    (ServletRequestAttributes)
                            RequestContextHolder.getRequestAttributes();

            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String token = request.getHeader("Authorization");

                if (token != null) {
                    requestTemplate.header("Authorization", token);
                }
            }
        };
    }
}
















//package com.example.demo;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import feign.auth.BasicAuthRequestInterceptor;
//
//@Configuration
//public class FeignConfig {
//
//    @Bean
//    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
//        // You should provide valid admin credentials here
//        return new BasicAuthRequestInterceptor("admin", "admin123");
//    }
// @Bean
//    public feign.Logger.Level feignLoggerLevel() {
//        return feign.Logger.Level.FULL;
//    }
//}
