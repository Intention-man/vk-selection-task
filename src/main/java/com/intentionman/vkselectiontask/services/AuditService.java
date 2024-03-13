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
            saveRequestData(request, false);
        } else {
            saveRequestData(request, true);
        }
    }

    public void saveRequestData(HttpServletRequest request, boolean hasUserAuthority) {
        requestRepositoriy.save(new RequestAudit(request.getMethod(), request.getServletPath(), hasUserAuthority));
    }

    public boolean checkUserHasNecessaryAuthority(HttpServletRequest request, UserDetails userDetails) {
        return userDetails
                .getAuthorities()
                .stream()
                .anyMatch(
                        grantedAuthority -> checkRoleHasNecessaryAuthority(request, grantedAuthority.getAuthority())
                );
    }

    public boolean checkRoleHasNecessaryAuthority(HttpServletRequest request, String roleName) {
        String method = request.getMethod();
        String path = request.getServletPath();

        return Role.valueOf(roleName).checkRequestPossibility(method, path);
    }
}
