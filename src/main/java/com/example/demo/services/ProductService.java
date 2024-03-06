package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exceptions.ProductArleadyExistsException;
import com.example.demo.model.Product;
import com.example.demo.repositories.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
    final ProductRepository productRepository;

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public void removeProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);

    }

    public boolean existProductByName(Product product) {
        return productRepository.existsByName(product.getName());
    }

    public void insertProduct(Product product) throws ProductArleadyExistsException {
        var productExist = existProductByName(product);
        if (productExist) {
            throw new ProductArleadyExistsException();
        } else {
            product.setId(null);
            productRepository.save(product);
        }

    }

    public void updateCurrentProduct(Product product, Long id) {
        product.setId(id);
        productRepository.save(product);
    }

}