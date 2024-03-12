package com.intentionman.vkselectiontask.services;

import com.intentionman.vkselectiontask.config.UtilConfig;
import com.intentionman.vkselectiontask.domain.dto.AuthResponse;
import com.intentionman.vkselectiontask.domain.dto.UserDto;
import com.intentionman.vkselectiontask.domain.entities.RequestAudit;
import com.intentionman.vkselectiontask.domain.entities.Role;
import com.intentionman.vkselectiontask.domain.entities.UserEntity;
import com.intentionman.vkselectiontask.repositories.RequestRepositoriy;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;


    /**
     * Регистрация пользователя
     *
     * @param userDto данные пользователя
     * @return токен
     */
    public AuthResponse signUp(UserDto userDto) {
        var user = UserEntity.builder()
                .username(userDto.getUsername())
                .password(UtilConfig.encoder().encode(userDto.getPassword()))
                .role(Role.valueOf(userDto.getRole()))
                .build();

        userService.create(user);
        var token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    /**
     * Аутентификация пользователя
     *
     * @param userDto данные пользователя
     * @return токен
     */
    public AuthResponse signIn(UserDto userDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userDto.getUsername(),
                userDto.getPassword()
        ));

        var user = userService.userDetailsService()
                .loadUserByUsername(userDto.getUsername());

        var token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }


}
