/*
 * package com.example.demo.config;
 * 
 * import feign.RequestInterceptor; import feign.RequestTemplate; import
 * org.springframework.context.annotation.Bean; import
 * org.springframework.context.annotation.Configuration; import
 * org.springframework.security.core.Authentication; import
 * org.springframework.security.core.context.SecurityContextHolder;
 * 
 * @Configuration public class FeignClientConfig {
 * 
 * @Bean public RequestInterceptor jwtForwardingInterceptor() { return new
 * RequestInterceptor() {
 * 
 * @Override public void apply(RequestTemplate template) {
 * 
 * Authentication authentication =
 * SecurityContextHolder.getContext().getAuthentication();
 * 
 * if (authentication == null || !authentication.isAuthenticated()) { return; //
 * No token available }
 * 
 * Object credentials = authentication.getCredentials();
 * 
 * if (credentials != null) {
 * 
 * String token = credentials.toString();
 * 
 * // Avoid double Bearer prefix if (!token.startsWith("Bearer ")) { token =
 * "Bearer " + token; }
 * 
 * template.header("Authorization", token); } } }; } }
 */