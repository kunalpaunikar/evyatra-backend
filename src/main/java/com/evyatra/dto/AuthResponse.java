package com.evyatra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

    private String token; // jwt token stored frontend.
    private String name;
    private String email;
    private String role;
}
