package com.example.demo.cotroller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.exceptions.ProductArleadyExistsException;
import com.example.demo.model.Product;
import com.example.demo.services.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
class ProductController {
    final ProductService productService;

    @GetMapping("/product")
    public String showAllProducts(Model model) {
        model.addAttribute("productList", productService.findAllProducts());
        model.addAttribute("title", "List of products");
        model.addAttribute("action", "/saveProduct");

        return "/product/products";
    }

    @GetMapping("/removeProduct")
    public String removeProduct(@RequestParam Long productId) {
        productService.removeProduct(productId);
        return "redirect:/product";
    }

    // todo:
    @GetMapping("/productDetail")
    public String showProductDetailByID(@RequestParam Long productId, Model model) {

        var optionalProduct = productService.findProductById(productId);

        if (optionalProduct.isPresent()) {
            model.addAttribute("product", optionalProduct.get());
            return "/product/product";
        } else {
            model.addAttribute("error", "Current product with id=" + productId + " doesn't exist");
            return "/error-page";
        }

    }

    // insert product
    @PostMapping("/saveProduct")
    public String saveProduct(@Valid Product productForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();

            if (productService.existProductByName(productForm)) {
                redirectAttributes.addFlashAttribute("error", "Product exists");
            } else {
                redirectAttributes.addFlashAttribute("errors", errors);
                redirectAttributes.addFlashAttribute("product", productForm);
            }
        } else {
            try {
                productService.insertProduct(productForm);
            } catch (ProductArleadyExistsException e) {
                redirectAttributes.addFlashAttribute("error", "Product arleady exists");
            }
        }

        return "redirect:/product";
    }

    // edycja productktu
    @GetMapping("/editProduct")
    public String editProduct(@RequestParam Long productId, Model model) {
        model.addAttribute("title", "Edit current product");
        model.addAttribute("action", "/editedProduct?productId=" + productId);

        return bindProductToModel(productId, model);

    }

    // zapisywanie edytowanego producktu
    @PostMapping("/editedProduct")
    public String saveEditedProdutc(@RequestParam Long productId, @Valid Product productForm) {

        productService.updateCurrentProduct(productForm, productId);
        var url = "redirect:/productDetail?productId=" + productId;
        return url;

    }

    private String bindProductToModel(Long productId, Model model) {
        var optionalProduct = productService.findProductById(productId);
        if (optionalProduct.isEmpty()) {
            model.addAttribute("error", "Product doesn't exist");
            return "/error-page";
        } else {
            var product = optionalProduct.get();
            model.addAttribute("product", product);
            return "/product/edit-product";
        }

    }

}
