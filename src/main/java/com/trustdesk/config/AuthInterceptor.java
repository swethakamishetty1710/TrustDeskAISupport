package com.trustdesk.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Value("${trustdesk.auth.token}")
    private String expectedToken;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        // Allow browser static resources
        String path = request.getRequestURI();

        if (!path.startsWith("/api/")) {
            return true;
        }

        String token =
                request.getHeader("X-TrustDesk-Token");

        if (expectedToken.equals(token)) {
            return true;
        }

        response.setStatus(
                HttpServletResponse.SC_UNAUTHORIZED
        );

        response.setContentType("application/json");

        response.getWriter().write(
                "{\"error\":\"Unauthorized\"}"
        );

        return false;
    }
}