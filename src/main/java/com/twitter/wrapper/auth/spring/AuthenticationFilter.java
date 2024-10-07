package com.twitter.wrapper.auth.spring;

import com.twitter.wrapper.auth.TokenKey;
import com.twitter.wrapper.auth.services.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;

    @Autowired
    public AuthenticationFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        String parsedPlainToken = parseBearerHeader(authorizationHeader);
        if(Strings.isEmpty(parsedPlainToken)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
        boolean authenticated = authService.authenticate(new TokenKey(parsedPlainToken));
        if(authenticated) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }

    private String parseBearerHeader(String authorizationHeader) {
        if(Strings.isEmpty(authorizationHeader)) {
            return null;
        }
        String[] splitHeader = authorizationHeader.split("Bearer ");
        if(splitHeader.length != 2) {
            return null;
        }
        return splitHeader[1];
    }
}
