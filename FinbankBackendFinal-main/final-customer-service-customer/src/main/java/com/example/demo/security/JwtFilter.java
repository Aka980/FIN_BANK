//package com.example.demo.security;
//
//import java.io.IOException;
//import java.util.Collections;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import com.example.demo.jwt.JwtUtil;
//
//@Component
//public class JwtFilter extends OncePerRequestFilter {
//
//@Autowired
//JwtUtil jwtUtil;
//
//@Override
//protected void doFilterInternal(
//HttpServletRequest request,
//HttpServletResponse response,
//FilterChain filterChain)
//
//throws ServletException, IOException{
//
//
//String path=request.getRequestURI();
//
//if(path.contains("/login")
//|| path.contains("/register")){
//
//filterChain.doFilter(request,response);
//return;
//}
//
//String header=
//request.getHeader("Authorization");
//
//
//if(header!=null
//&& header.startsWith("Bearer ")){
//
//String token=
//header.substring(7);
//
//
//if(jwtUtil.validateToken(token)){
//
//String username=
//jwtUtil.extractUsername(token);
//
//String role=
//jwtUtil.extractRole(token);
//
//
//UsernamePasswordAuthenticationToken auth=
//
//new UsernamePasswordAuthenticationToken(
//
//username,
//
//null,
//
//Collections.singletonList(
//
//new SimpleGrantedAuthority(role)
//
//)
//
//);
//
//
//SecurityContextHolder.getContext()
//.setAuthentication(auth);
//
//}
//
//}
//
//filterChain.doFilter(request,response);
//
//}
//
//}

package com.example.demo.security;

import java.io.IOException;
import java.util.Collections;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.jwt.JwtUtil;

@Component
public class JwtFilter extends OncePerRequestFilter {

        @Autowired
        JwtUtil jwtUtil;

        @Override
        protected void doFilterInternal(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        FilterChain filterChain)

                        throws ServletException, IOException {

                String path = request.getRequestURI();

                // Skip login & register
                if (path.contains("/login")
                                || path.contains("/register")) {

                        filterChain.doFilter(request, response);
                        return;
                }

                String header = request.getHeader("Authorization");
                if (header != null && header.regionMatches(true, 0, "Bearer ", 0, 7)) {
                        String token = header.substring(7);

                        if (jwtUtil.validateToken(token)) {
                                String subject = jwtUtil.extractSubject(token);
                                String role = jwtUtil.extractRole(token);

                                if (role != null) {
                                        role = role.trim().toUpperCase();
                                }

                                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                                subject,
                                                null,
                                                Collections.singletonList(new SimpleGrantedAuthority(role)));
                                // DEBUG START
                                System.out.println("AUTHORITY SET: " + role);
                                // DEBUG END

                                SecurityContextHolder.getContext()
                                                .setAuthentication(auth);

                        }

                }

                filterChain.doFilter(request, response);

        }

}