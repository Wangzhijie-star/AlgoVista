package com.algovista.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.algovista.common.result.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionAuthInterceptor implements HandlerInterceptor {
    private final ObjectMapper objectMapper;

    public SessionAuthInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object userId = request.getSession(false) == null
                ? null
                : request.getSession(false).getAttribute(SessionKeys.CURRENT_USER_ID);
        if (userId != null) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail(401, "请先登录")));
        return false;
    }
}
