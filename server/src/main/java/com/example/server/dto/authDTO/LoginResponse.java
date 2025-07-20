package com.example.server.dto.authDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String token;
    private long id;

    @Override
    public String toString() {
        return "ID: " + id + ". TOKEN: " + token;
    }
}
