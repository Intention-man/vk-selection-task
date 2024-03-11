package com.intentionman.vkselectiontask.services;


import com.intentionman.vkselectiontask.config.MapperConfig;
import com.intentionman.vkselectiontask.domain.entities.Role;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final Map<String, Long> tokenStorage = new HashMap<>(); // token -> userId
    private final JwtProvider jwtProvider;
    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;
    private final UserMapperImpl userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            return buildDefaultUserDetails();
        }
    }

    public String login(UserDto userDto) {
        return addTokenForUser(userDto);
    }

    public String registration(UserDto userDto) {
        // TODO проверка на валидную роль
        // TODO как сделать доступ к созданию ROLE_ADMIN ?
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

    public Optional<UserDto> findByLogin(String login) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(login);
        return optionalUser.map(userMapper::entityToUserDto);
    }

    public boolean isUserExists(UserDto userDto) {
        UserEntity userEntity = userMapper.userDtoToEntity(userDto);
        Optional<UserEntity> optionalUser = userRepository.findByUsername(userEntity.getUsername());
        if (optionalUser.isPresent()) {
            UserEntity foundUser = optionalUser.get();
            return MapperConfig.encoder().matches(userEntity.getPassword(), foundUser.getPassword());
        }
        return false;
    }

    public boolean isLoginAndPasswordValid(UserDto userDto) {
        return userDto.getUsername().length() >= 6 && userDto.getPassword().length() >= 6;
    }

    public UserDetails buildDefaultUserDetails() {
        return new UserEntity(0L, "default", "default", Role.ROLE_DEFAULT);
    }

    /**
     * Сохранение пользователя
     *
     * @return сохраненный пользователь
     */
    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }


    /**
     * Создание пользователя
     *
     * @return созданный пользователь
     */
    public UserEntity create(UserEntity user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            // Заменить на свои исключения
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
        return save(user);
    }

    /**
     * Получение пользователя по имени пользователя
     *
     * @return пользователь
     */
    public UserEntity getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

    }

    /**
     * Получение пользователя по имени пользователя
     * <p>
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    /**
     * Получение текущего пользователя
     *
     * @return текущий пользователь
     */
    public UserEntity getCurrentUser() {
        // Получение имени пользователя из контекста Spring Security
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }
}