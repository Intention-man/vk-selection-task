package com.intentionman.vkselectiontask.services;

import com.intentionman.vkselectiontask.config.UtilConfig;
import com.intentionman.vkselectiontask.domain.dto.AuthResponse;
import com.intentionman.vkselectiontask.domain.dto.UserDto;
import com.intentionman.vkselectiontask.domain.entities.Role;
import com.intentionman.vkselectiontask.domain.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

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
        try {
            Role role = Role.valueOf(userDto.getRole());
            var user = UserEntity.builder()
                    .username(userDto.getUsername())
                    .password(UtilConfig.encoder().encode(userDto.getPassword()))
                    .role(role)
                    .build();

            userService.create(user);
            var token = jwtService.generateToken(user);
            return new AuthResponse(token);
        } catch (Exception e) {
            return null;
        }
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
