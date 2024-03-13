package com.intentionman.vkselectiontask.services;

import com.intentionman.vkselectiontask.domain.entities.RequestAudit;
import com.intentionman.vkselectiontask.domain.entities.Role;
import com.intentionman.vkselectiontask.repositories.RequestRepositoriy;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final Set<String> methodsToFilter = Set.of("GET", "POST", "PUT", "PATCH", "DELETE");

    private final RequestRepositoriy requestRepositoriy;

    public boolean checkShouldProxy(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getServletPath();
        if (!methodsToFilter.contains(method.toUpperCase()))
            return false;
        // если запрос доступен ROLE_DEFAULT (то есть всем ролям), не фильтруем
        return !Role.ROLE_DEFAULT.checkContainsRegPath(path);
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
