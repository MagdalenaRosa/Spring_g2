package com.example.demo.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_address_pivot", joinColumns = {
            @JoinColumn(name = "userID")
    }, inverseJoinColumns = { @JoinColumn(name = "addressID") })
    private List<Adresses> adress;

    @OneToMany(cascade = CascadeType.ALL)
   @JoinTable(name = "user_phone_pivot", joinColumns = {
            @JoinColumn(name = "userID")
    }, inverseJoinColumns = { @JoinColumn(name = "phoneID") })
    private List<PhoneNumber> phoneNumber;

}
