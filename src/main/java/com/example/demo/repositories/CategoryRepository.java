package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    @Query("SELECT n FROM Category n WHERE "
            + "CONCAT(n.name, n.desc)"
            + "LIKE %?1%")
    // @Query("SELECT n FROM Category n WHERE n.name LIKE %?1% OR n.desc LIKE %?1%")
    List<Category> findAll(String keyword);
}
