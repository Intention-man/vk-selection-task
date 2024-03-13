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
        HttpStatus authStatus = userService.tryAuth(userDto, optionalUser);
        if (authStatus == HttpStatus.OK && optionalUser.isPresent()) {
            return new ResponseEntity<>(authService.signIn(userDto), authStatus);
        }
        return new ResponseEntity<>(authStatus);
    }

    @PostMapping("/registration")
    public ResponseEntity<AuthResponse> registration(@RequestBody UserDto userDto) {
        if (userService.isUserExist(userDto))
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        AuthResponse authResponse = authService.signUp(userDto);
        if (authResponse == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }
}
