package com.intentionman.vkselectiontask.security;

import com.intentionman.vkselectiontask.services.AuditService;
import com.intentionman.vkselectiontask.services.JwtService;
import com.intentionman.vkselectiontask.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private JwtService jwtService;
    private UserService userService;
    private AuditService auditService;
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION = "Authorization";


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        boolean canContinue = false;
        // Получаем токен из заголовка
        var authHeader = request.getHeader(AUTHORIZATION);
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
            boolean shouldProxy = auditService.checkShouldProxy(request);
            auditService.saveRequestData(request, !shouldProxy);
            if (shouldProxy){
                response.setStatus(403);
            } else {
                canContinue = true;
            }
        } else {
            // Обрезаем префикс и получаем имя пользователя из токена
            var token = authHeader.substring(BEARER_PREFIX.length());
            var username = jwtService.extractUserName(token);

            if (StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService
                        .userDetailsService()
                        .loadUserByUsername(username);

                // Если токен валиден, то аутентифицируем пользователя
                if (jwtService.isTokenValid(token, userDetails)) {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authToken);
                    SecurityContextHolder.setContext(context);
                }
                boolean hasUserAuthority = auditService.checkUserHasNecessaryAuthority(request, userDetails);
                auditService.saveRequestData(request, hasUserAuthority);
                if (!hasUserAuthority){
                    response.setStatus(403);
                } else {
                    canContinue = true;
                }
            }
        }
        // если запрос корректный и прав достаточно, передаем его интерцептору и контроллерам
        if (canContinue)
            filterChain.doFilter(request, response);
    }
}
