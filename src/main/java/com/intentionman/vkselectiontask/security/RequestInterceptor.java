package com.intentionman.vkselectiontask.security;


import com.intentionman.vkselectiontask.domain.entities.RequestAudit;
import com.intentionman.vkselectiontask.repositories.RequestRepositoriy;
import com.intentionman.vkselectiontask.services.AuditService;
import com.intentionman.vkselectiontask.services.AuthService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.logging.Logger;

@Component
@AllArgsConstructor
public class RequestInterceptor implements HandlerInterceptor {
    private AuditService auditService;
    private RequestRepositoriy requestRepositoriy;
    private Logger logger;
    private static final String COMMON_PATH = "https://jsonplaceholder.typicode.com";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        logger.warning("preHandle");
        try {
            if (auditService.shouldFilter(request)) {
                String endpoint = request.getServletPath();
                request.setAttribute("url", COMMON_PATH + endpoint);
            }
            return true;
        } catch (JwtException e) {
            response.setStatus(401);
            return false;
        } catch (Exception e) {
            response.setStatus(500);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        logger.warning("postHandle");
        response.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD");
        response.addHeader("Access-Control-Max-Age", "1209600");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        logger.warning("afterCompletion");
    }

//    @Autowired
//    public void setAuditService(AuditService auditService) {
//        this.auditService = auditService;
//    }
//
//    @Autowired
//    public void setRequestRepositoriy(RequestRepositoriy requestRepositoriy) {
//        this.requestRepositoriy = requestRepositoriy;
//    }
//
//    @Autowired
//    public void setLogger(Logger logger) {
//        this.logger = logger;
//    }
}