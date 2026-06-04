package com.evyatra.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Name required hai")
    private String name;

    @Email(message = "Valid email do")
    @NotBlank(message = "Email required hai")
    private String email;

    @Size(min = 6, message = "Password minimum 6 characters ka hona chahiye")
    private String password;

    private String phone;

    // Getters
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhone() { return phone; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPhone(String phone) { this.phone = phone; }
}