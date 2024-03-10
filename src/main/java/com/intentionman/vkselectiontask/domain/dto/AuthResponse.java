package com.intentionman.vkselectiontask.domain.dto;


import com.intentionman.vkselectiontask.domain.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
}