package com.intentionman.vkselectiontask.controllers;


import com.intentionman.vkselectiontask.domain.dto.AuthResponse;
import com.intentionman.vkselectiontask.domain.dto.UserDto;
import com.intentionman.vkselectiontask.services.AuthService;
import com.intentionman.vkselectiontask.services.UserService;
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
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserDto userDto) {
        final Optional<UserDto> optionalUser = userService.findByLogin(userDto.getUsername());
        HttpStatus authStatus = authService.tryAuth(userDto, optionalUser);
        if (authStatus == HttpStatus.OK && optionalUser.isPresent()) {
            String token = authService.login(optionalUser.get());
            return new ResponseEntity<>(new AuthResponse(token), authStatus);
        }
        return new ResponseEntity<>(authStatus);
    }

    @PostMapping("/registration")
    public ResponseEntity<AuthResponse> registration(@RequestBody UserDto userDto) {
        if (userService.isUserExists(userDto))
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        if (!userService.isLoginAndPasswordValid(userDto))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        String token = authService.registration(userDto);
        return new ResponseEntity<>(new AuthResponse(token), HttpStatus.CREATED);
    }
}
