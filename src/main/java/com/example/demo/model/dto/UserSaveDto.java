package com.example.demo.model.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserSaveDto {
    @NotBlank(message = "User name is required")
    private String firstName;
    @NotBlank(message = "User surname is required")
    private String lastName;
    @NotBlank(message = "User email is required")
    private String email;
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$", message = "Minimum eight characters, at least one letter, one number and one special charater")
    private String password;
    @NotBlank(message = "Zip Code is required")
    private String zipCode;
    @NotBlank(message = "Street name is required")
    private String street;
    @NotBlank(message = "City name is required")
    private String city;
    @NotBlank(message = "Prefix number is required")
    private String prefix;
    @NotBlank(message = "Phone number is required")
    private String phone;
}
