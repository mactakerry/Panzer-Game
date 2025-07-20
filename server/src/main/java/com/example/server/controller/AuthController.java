package com.example.server.controller;

import com.example.server.dto.authDTO.LoginResponse;
import com.example.server.dto.authDTO.LoginUserDTO;
import com.example.server.dto.authDTO.RegistrationUserDTO;
import com.example.server.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/public/reg")
    public ResponseEntity<Void> createUser(@Valid @RequestBody RegistrationUserDTO dto) {
        authService.register(dto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/public/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginUserDTO dto) {
        LoginResponse response = authService.login(dto);

        return ResponseEntity.ok(response);
    }
}
