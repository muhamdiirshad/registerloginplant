package com.example.registerloginplant.controller;

import com.example.registerloginplant.entity.Product;
import com.example.registerloginplant.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Display products by category
    @GetMapping("/category/{categoryName}")
    public String showProductsByCategory(@PathVariable String categoryName, Model model) {
        List<Product> products = productService.getProductsByCategory(categoryName);
        model.addAttribute("products", products);
        return "products/category"; // Return the appropriate view (e.g., category.html)
    }

    @GetMapping("/all")
    public String showAllProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "products/all";
    }
    // Add to cart functionality
    @PostMapping("/cart")
    public String addToCart(@RequestParam("productId") Long productId, HttpSession session) {
        // Implement add to cart logic here
        return "redirect:/home"; // Redirect to the user home page after adding to cart
    }
//
//    @GetMapping("/succulents")
//    public String getSucculents(Model model) {
//        List<Product> succulents = productService.getAllProducts();
//        model.addAttribute("products", succulents);
//        return "succulents";
//    }
}
