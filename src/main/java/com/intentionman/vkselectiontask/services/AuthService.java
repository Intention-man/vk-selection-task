package com.intentionman.vkselectiontask.services;


import com.intentionman.vkselectiontask.config.MapperConfig;
import com.intentionman.vkselectiontask.domain.UserDetailBean;
import com.intentionman.vkselectiontask.domain.dto.UserDto;
import com.intentionman.vkselectiontask.domain.entities.UserEntity;
import com.intentionman.vkselectiontask.mappers.UserMapperImpl;
import com.intentionman.vkselectiontask.repositories.UserRepository;
import com.intentionman.vkselectiontask.security.JwtDecoder;
import com.intentionman.vkselectiontask.security.JwtProvider;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final Map<String, Long> tokenStorage = new HashMap<>(); // token -> userId
    private final JwtProvider jwtProvider;
    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;
    private final UserMapperImpl userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        return optionalUser.map(UserDetailBean::new).orElse(null);
    }

    public String login(UserDto userDto) {
        return addTokenForUser(userDto);
    }

    public String registration(UserDto userDto) {
        userDto.setPassword(MapperConfig.encoder().encode(userDto.getPassword()));
        userRepository.save(userMapper.userDtoToEntity(userDto));
        return addTokenForUser(userDto);
    }

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