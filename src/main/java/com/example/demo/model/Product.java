package com.example.demo.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(min = 5, max = 255, message = "min length is 5 charachers max length is 255 characters")
    private String name;
    @NotBlank(message = "description is required")
    @Column(name = "description", length = 1023)
    @Size(min = 10, max = 1023, message = "min length is 10 charachers max length is 1023 characters")
    private String desc;

    @Column(length = 1023)
    private String imgUri;

    @NotNull(message = "price is required")
    private BigDecimal price;

}
