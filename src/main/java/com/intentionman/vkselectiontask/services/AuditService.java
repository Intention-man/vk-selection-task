package com.intentionman.vkselectiontask.services;

import com.intentionman.vkselectiontask.domain.entities.RequestAudit;
import com.intentionman.vkselectiontask.domain.entities.Role;
import com.intentionman.vkselectiontask.repositories.RequestRepositoriy;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final Set<String> methodsToFilter = Set.of("GET", "POST", "PUT", "PATCH", "DELETE");
    private final Set<String> filterIgnorePaths = Set.of("/auth/.*");

    private final RequestRepositoriy requestRepositoriy;
    /**
     * Проксирование не нужно, если это OPTIONS запрос или запрос с вида /auth/**
     * Остальные случаи - это проксирование
     */
    public boolean shouldFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        if (!methodsToFilter.contains(request.getMethod().toUpperCase()))
            return false;
        for (String regPath : filterIgnorePaths) {
            if (Pattern.matches(regPath, path)) {
                return false;
            }
        }
        return true;
    }

    public void saveRequestDataWithoutToken(HttpServletRequest request) {
        if (shouldFilter(request)) {
            requestRepositoriy.save(new RequestAudit(request.getMethod(), request.getServletPath(), false));
        } else {
            requestRepositoriy.save(new RequestAudit(request.getMethod(), request.getServletPath(), true));
        }
    }

    public void saveProxyRequestData(HttpServletRequest request, UserDetails userDetails) {
        boolean hasAuthority = userDetails
                .getAuthorities()
                .stream()
                .anyMatch(
                        grantedAuthority -> hasNecessaryAuthority(request, grantedAuthority.getAuthority())
                );
        requestRepositoriy.save(new RequestAudit(request.getMethod(), request.getServletPath(), hasAuthority));
    }

    public boolean hasNecessaryAuthority(HttpServletRequest request, String roleName) {
        String path = request.getServletPath();
        return Role.valueOf(roleName).checkRequestPossibility(path);
    }
}
