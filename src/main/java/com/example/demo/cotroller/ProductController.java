package com.example.demo.cotroller;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String showAllProducts(Model model, @Param("keyword") String keyword){
        return showAllProductsByPageNumber(model, keyword, 1);
        
    }


    @GetMapping("/product/{pageNumber}")
    public String showAllProductsByPageNumber(Model model, @Param("keyword") String keyword, @PathVariable int pageNumber) {

        Page<Product> producktPage=productService.findAllProducts(keyword, pageNumber);
        int totalPages= producktPage.getTotalPages();
        model.addAttribute("productList", producktPage.getContent());
        model.addAttribute("title", "List of products");
        model.addAttribute("action", "/saveProduct");
        model.addAttribute("categories", productService.findAllCategories());
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchAction", "/product");
        model.addAttribute("items", producktPage.getTotalElements());
        if(totalPages>0){
            model.addAttribute("pages", totalPages);
        }else{
            model.addAttribute("pages", 1);
        }
        model.addAttribute("currentPage", pageNumber);
        return "/product/products";
    }
@Secured({"ROLE_MANAGER","ROLE_ADMIN"})
    @GetMapping("/removeProduct/{productId}")
    public String removeProduct(@PathVariable Long productId) {
        productService.removeProduct(productId);
        return "redirect:/product";
    }

    // todo:

    @GetMapping("/productDetail/{productId}")
    public String showProductDetailByID(@PathVariable Long productId, Model model) {

        var optionalProduct = productService.findProductById(productId);

        if (optionalProduct.isPresent()) {
            model.addAttribute("product", optionalProduct.get());
            return "/product/product";
        } else {
            model.addAttribute("error", "Current product with id=" + productId + " doesn't exist");
            model.addAttribute("errorAction", "/product");
            model.addAttribute("return", "Return to list of products");
            return "/error-page";
        }

    }
    @Secured({"ROLE_MANAGER","ROLE_ADMIN"})    // insert product
    @PostMapping("/saveProduct")
    public String saveProduct(@Valid Product productForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();

            redirectAttributes.addFlashAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("product", productForm);

        } else {
            try {
                productService.insertProduct(productForm);
            } catch (ProductArleadyExistsException e) {
                redirectAttributes.addFlashAttribute("error", "Product arleady exists");
                redirectAttributes.addFlashAttribute("product", productForm);
            }
        }

        return "redirect:/product";
    }

    // edycja productktu
    @Secured({"ROLE_MANAGER","ROLE_ADMIN"})    @GetMapping("/editProduct/{productId}")
    public String editProduct(@PathVariable Long productId, Model model) {
        model.addAttribute("title", "Edit current product");
        model.addAttribute("categories", productService.findAllCategories());

        model.addAttribute("action", "/editedProduct/" + productId);

        return bindProductToModel(productId, model);

    }

    // zapisywanie edytowanego producktu
    @Secured({"ROLE_MANAGER","ROLE_ADMIN"})
        @PostMapping("/editedProduct/{productId}")
    public String saveEditedProdutc(@PathVariable Long productId, @Valid Product productForm,
            BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            redirectAttributes.addFlashAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("product", productForm);
            return "redirect:/editProduct/" + productId;
        } else {
            productService.updateCurrentProduct(productForm, productId);
            var url = "redirect:/productDetail/" + productId;
            return url;
        }

    }

    private String bindProductToModel(Long productId, Model model) {
        var optionalProduct = productService.findProductById(productId);
        if (optionalProduct.isEmpty()) {
            model.addAttribute("error", "Product doesn't exist");
            model.addAttribute("errorAction", "/product");
            model.addAttribute("return", "Return to list of products");
            return "/error-page";
        } else {
            var product = optionalProduct.get();
            model.addAttribute("product", product);
            return "/product/edit-product";
        }

    }

}

// @NotNull -> null
// @NotEmpty -> null, ""
// @NotBlank-> "Test"->