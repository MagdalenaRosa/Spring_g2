package com.example.demo.cotroller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

}
