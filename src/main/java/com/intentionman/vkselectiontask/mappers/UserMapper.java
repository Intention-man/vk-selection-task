package com.intentionman.vkselectiontask.mappers;


import com.intentionman.vkselectiontask.domain.dto.UserDto;
import com.intentionman.vkselectiontask.domain.entities.UserEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    public UserDto entityToUserDto(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserDto.class);
    }

    public UserEntity userDtoToEntity(UserDto userDto) {
        return modelMapper.map(userDto, UserEntity.class);
    }
}
