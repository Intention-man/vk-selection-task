package com.intentionman.vkselectiontask.services;


import com.intentionman.vkselectiontask.config.MapperConfig;
import com.intentionman.vkselectiontask.domain.dto.UserDto;
import com.intentionman.vkselectiontask.domain.entities.UserEntity;
import com.intentionman.vkselectiontask.mappers.UserMapperImpl;
import com.intentionman.vkselectiontask.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class UserServiceImpl {
    private final UserRepository userRepository;
    private final UserMapperImpl userMapper;

    public UserDto save(UserDto userDto) {
        UserEntity userEntity = userMapper.userDtoToEntity(userDto);
        userEntity.setPassword(MapperConfig.encoder().encode(userEntity.getPassword()));
        return userMapper.entityToUserDto(userRepository.save(userEntity));
    }

    public Optional<UserDto> findById(Long userId) {
        Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);
        return optionalUserEntity.map(userMapper::entityToUserDto);
    }

    public Optional<UserDto> findByLogin(String login) {
        Optional<UserEntity> optionalUser = userRepository.findByLogin(login);
        return optionalUser.map(userMapper::entityToUserDto);
    }

    public boolean isExists(Long userId) {
        return userRepository.existsById(userId);
    }

    public boolean isUserExists(UserDto userDto) {
        UserEntity userEntity = userMapper.userDtoToEntity(userDto);
        Optional<UserEntity> optionalUser = userRepository.findByLogin(userEntity.getLogin());
        if (optionalUser.isPresent()) {
            UserEntity foundUser = optionalUser.get();
            return MapperConfig.encoder().matches(userEntity.getPassword(), foundUser.getPassword());
        }
        return false;
    }

    public List<UserDto> findAll() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false).map(userMapper::entityToUserDto).toList();
    }

    public UserDto partialUpdate(Long userId, UserDto userDto) {
        userDto.setUserId(userId);
        return userRepository.findById(userId).map(existingUser -> {
            Optional.ofNullable(userDto.getLogin()).ifPresent(existingUser::setLogin);
            Optional.ofNullable(MapperConfig.encoder().encode(userDto.getPassword())).ifPresent(existingUser::setPassword);
            return userMapper.entityToUserDto(userRepository.save(existingUser));
        }).orElseThrow(() -> new RuntimeException("User doesn't exists"));
    }

    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    public boolean isLoginAndPasswordValid(UserDto userDto) {
        return userDto.getLogin().length() >= 6 && userDto.getPassword().length() >= 6;
    }
}
