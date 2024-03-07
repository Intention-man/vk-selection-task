package com.intentionman.vkselectiontask.controllers;


import com.intentionman.vkselectiontask.domain.dto.UserDto;
import com.intentionman.vkselectiontask.domain.dto.AuthResponse;
import com.intentionman.vkselectiontask.services.AuthService;
import com.intentionman.vkselectiontask.services.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserServiceImpl userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserDto userDto) {
        final Optional<UserDto> optionalUser = userService.findByLogin(userDto.getLogin());
        HttpStatus authStatus = authService.tryAuth(userDto, optionalUser);
        if (authStatus == HttpStatus.OK && optionalUser.isPresent()) {
            String token = authService.addTokenForUser(optionalUser.get());
            return new ResponseEntity<>(new AuthResponse(token, optionalUser.get()), authStatus);
        }
        return new ResponseEntity<>(authStatus);
    }

    @PostMapping("/registration")
    public ResponseEntity<AuthResponse> registration(@RequestBody UserDto userDto) {
        if (userService.isUserExists(userDto))
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        if (!userService.isLoginAndPasswordValid(userDto))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        UserDto savedUser = userService.save(userDto);
        String token = authService.addTokenForUser(savedUser);
        return new ResponseEntity<>(new AuthResponse(token, savedUser), HttpStatus.CREATED);
    }
}
