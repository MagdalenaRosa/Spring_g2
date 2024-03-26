package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.apache.tomcat.websocket.PerMessageDeflate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.exceptions.ProductArleadyExistsException;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.repositories.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
    final ProductRepository productRepository;
    final CategoryRepository categoryRepository;

    public Page<Product> findAllProducts(String keyword,int pageNumber) {
        Pageable page= PageRequest.of(pageNumber-1,5);
        if (keyword != null) {
            return productRepository.findAll(keyword,page);
        } else {
            return productRepository.findAll(page);
        }

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

    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Product> findProductByCategoryId(Long categoryId) {
        return productRepository.findAllByCategoryId(categoryId);
    }

}
