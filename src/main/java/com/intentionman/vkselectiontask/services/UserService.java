package com.intentionman.vkselectiontask.services;


import com.intentionman.vkselectiontask.config.UtilConfig;
import com.intentionman.vkselectiontask.domain.dto.UserDto;
import com.intentionman.vkselectiontask.domain.entities.Role;
import com.intentionman.vkselectiontask.domain.entities.UserEntity;
import com.intentionman.vkselectiontask.mappers.UserMapperImpl;
import com.intentionman.vkselectiontask.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
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

    public HttpStatus tryAuth(@NonNull UserDto userCredentials, Optional<UserDto> optionalUser) {
        if (optionalUser.isEmpty())
            return HttpStatus.NOT_FOUND;
        if (UtilConfig.encoder().matches(userCredentials.getPassword(), optionalUser.get().getPassword()))
            return HttpStatus.OK;

        return HttpStatus.FORBIDDEN;
    }

    public Optional<UserDto> findByLogin(String login) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(login);
        return optionalUser.map(userMapper::entityToUserDto);
    }

    public boolean isLoginDataCorrect(UserDto userDto) {
        UserEntity userEntity = userMapper.userDtoToEntity(userDto);
        Optional<UserEntity> optionalUser = userRepository.findByUsername(userEntity.getUsername());
        if (optionalUser.isPresent()) {
            UserEntity foundUser = optionalUser.get();
            return UtilConfig.encoder().matches(userEntity.getPassword(), foundUser.getPassword());
        }
        return false;
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

}