package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserSaveDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String zipCode;
    private String street;
    private String city;
    private String prefix;
    private String phone;
}
