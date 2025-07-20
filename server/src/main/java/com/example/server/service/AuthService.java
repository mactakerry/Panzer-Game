package com.example.server.service;


import com.example.server.dto.authDTO.LoginResponse;
import com.example.server.dto.authDTO.LoginUserDTO;
import com.example.server.dto.authDTO.RegistrationUserDTO;
import com.example.server.exception.UsernameExistsException;
import com.example.server.model.entity.User;
import com.example.server.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationProvider authenticationProvider;


    public void register(RegistrationUserDTO dto) {
        if (userService.existByUsername(dto.getUsername())) {
            throw new UsernameExistsException("Username already exists");
        }
        User user = new User(dto.getUsername(), passwordEncoder.encode(dto.getPassword()));
        userService.saveUser(user);
    }

    public LoginResponse login(LoginUserDTO dto) {
        User user = userService.loadUserByUsername(dto.getUsername());

        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

        String token = jwtService.generateToken(user);
        long id = user.getId();

        LoginResponse response = new LoginResponse();
        response.setId(id);
        response.setToken(token);

        return response;
    }
}
