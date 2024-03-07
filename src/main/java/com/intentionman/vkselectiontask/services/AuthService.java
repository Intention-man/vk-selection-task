package com.intentionman.vkselectiontask.services;


import com.intentionman.vkselectiontask.config.MapperConfig;
import com.intentionman.vkselectiontask.domain.dto.UserDto;
import com.intentionman.vkselectiontask.mappers.UserMapperImpl;
import com.intentionman.vkselectiontask.security.JwtDecoder;
import com.intentionman.vkselectiontask.security.JwtProvider;
import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Getter
    private final Map<String, Long> tokenStorage = new HashMap<>(); // token -> userId
    private final JwtProvider jwtProvider;
    private final JwtDecoder jwtDecoder;

    public HttpStatus tryAuth(@NonNull UserDto userCredentials, Optional<UserDto> optionalUser) {
        if (optionalUser.isEmpty())
            return HttpStatus.NOT_FOUND;

        if (MapperConfig.encoder().matches(userCredentials.getPassword(), optionalUser.get().getPassword()))
            return HttpStatus.OK;

        return HttpStatus.FORBIDDEN;
    }

    public String addTokenForUser(UserDto foundUser) {
        final String accessToken = jwtProvider.generateAccessToken(foundUser);
        tokenStorage.put(accessToken, foundUser.getUserId());
        return accessToken;
    }


    public Long userIdFromStorage(String rawToken) {
        if (tokenStorage.containsKey(rawToken)) {
            return tokenStorage.get(rawToken);
        }
        return -1L;
    }

    public Long userIdFromToken(String rawToken) {
        Claims claims = jwtDecoder.decodeToken(rawToken);
        if (claims != null) return claims.get("userId", Long.class);
        return null; // if token expired
    }
}