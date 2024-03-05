package com.example.demo.cotroller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Product;

@Controller
public class ProductController {

    List<Product> productList = new ArrayList<>();

    ProductController() {
        var p1 = new Product(1, "Phone", "desc", "imgage", BigDecimal.valueOf(240.99));
        productList.add(p1);
        productList.add(new Product(2, "TV", "desc", "imgage", BigDecimal.valueOf(240.99)));
        // productList.addAll(List.of(p1,p2));

    }

    @GetMapping("/product")
    public String showAllProducts(Model model) {
        model.addAttribute("productList", productList);
        model.addAttribute("title", "List of products");
        model.addAttribute("action", "/saveProduct");

        return "/product/products";
    }

    @GetMapping("/removeProduct")
    public String removeProduct(@RequestParam Integer productId) {
        productList.removeIf(exiistProduct -> exiistProduct.getId().equals(productId));
        return "redirect:/product";
    }

    // todo:
    @GetMapping("/productDetail")
    public String showProductDetailByID(@RequestParam Integer productId, Model model) {

        var optionalProduct = productList.stream()
                .filter(product -> product.getId().equals(productId)).findFirst();

        if (optionalProduct.isPresent()) {
            model.addAttribute("product", optionalProduct.get());
        }
        return "/product/product";

    }

    // insert product
    @PostMapping("/saveProduct")
    public String saveProduct(Product productForm) {
        var productExist = productList.stream().map(product -> product.getName())
                .anyMatch(dbProduct -> productForm.getName().equals(dbProduct));

        if (!productExist) {
            var nextId = 1;
            if (!productList.isEmpty()) {
                var lastId = productList.size() - 1;
                nextId = productList.get(lastId).getId() + 1;
            }
            productForm.setId(nextId);
            productList.add(productForm);

        }

        return "redirect:/product";
    }

    // edycja productktu
    @GetMapping("/editProduct")
    public String editProduct(@RequestParam Integer productId, Model model) {
        model.addAttribute("title", "Edit current product");
        model.addAttribute("action", "/editedProduct?productId=" + productId);

        bindProductToModel(productId, model);
        return "/product/edit-product";
    }

    // zapisywanie edytowanego producktu
    @PostMapping("/editedProduct")
    public String saveEditedProdutc(@RequestParam Integer productId, Product productForm) {
        productList = productList.stream().map(
                product -> {
                    if (product.getId().equals(productId)) {
                        productForm.setId(productId);
                        return productForm;
                    } else {
                        return product;
                    }
                }).toList();
        var url = "redirect:/productDetail?productId=" + productId;
        return url;

    }

    private void bindProductToModel(Integer productId, Model model) {
        var optionalProduct = productList.stream().filter(dbProduct -> dbProduct.getId().equals(productId)).findFirst();
        if (optionalProduct.isEmpty()) {
            // todo:
        } else {
            var product = optionalProduct.get();
            model.addAttribute("product", product);
        }

    }

}
