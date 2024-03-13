package com.intentionman.vkselectiontask.security;


import com.intentionman.vkselectiontask.services.AuditService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@AllArgsConstructor
public class RequestInterceptor implements HandlerInterceptor {
    private AuditService auditService;
    private static final String COMMON_PATH = "https://jsonplaceholder.typicode.com";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            if (auditService.shouldFilter(request)) {
                String endpoint = request.getServletPath();
                request.setAttribute("url", COMMON_PATH + endpoint);
            }
            return true;
        } catch (Exception e) {
            response.setStatus(500);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        response.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD");
        response.addHeader("Access-Control-Max-Age", "1209600");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }
}