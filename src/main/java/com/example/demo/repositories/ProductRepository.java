package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.Product;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByName(String name);

    // select * from Product where categoryid =?
    List<Product> findAllByCategoryId(Long id);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
    List<Product> findAll(String keyword);

}
