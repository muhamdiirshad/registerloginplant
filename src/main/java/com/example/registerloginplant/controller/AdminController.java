package com.example.registerloginplant.controller;


import com.example.registerloginplant.entity.Category;
import com.example.registerloginplant.entity.Order;
import com.example.registerloginplant.entity.Product;
import com.example.registerloginplant.entity.User;
import com.example.registerloginplant.service.CategoryService;
import com.example.registerloginplant.service.OrderService;
import com.example.registerloginplant.service.ProductService;
import com.example.registerloginplant.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/admin")
    public String adminHome(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null || !loggedInUser.getRole().equals("admin")) {
            return "redirect:/login";
        }

        model.addAttribute("user", loggedInUser);
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin";
    }

    @GetMapping("/user/orders")
    public String getUserOrders(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<Order> userOrders = orderService.getOrdersByUserId(loggedInUser.getId());
        model.addAttribute("userOrders", userOrders);
        return "user-orders";
    }

    @GetMapping("/add-product")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "add-product";
    }

    @PostMapping("/add-product")
    public String addProduct(@ModelAttribute Product product, @RequestParam("categoryName") String categoryName, @RequestParam("imageFile") MultipartFile imageFile, Model model) throws IOException {
        Category category = categoryService.findOrCreateCategory(categoryName);
        product.setCategory(category);

        if (!imageFile.isEmpty()) {
            String imageUrl = productService.saveImage(imageFile);
            product.setImage(imageUrl);
        }

        productService.addProduct(product);
        model.addAttribute("message", "Product added successfully!");
        return "redirect:/admin";
    }

    @GetMapping("/edit-product/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "edit-product";
    }

    @PostMapping("/edit-product/{id}")
    public String editProduct(@PathVariable Long id, @ModelAttribute Product product, @RequestParam("imageFile") MultipartFile imageFile, Model model) throws IOException {
        Product existingProduct = productService.getProductById(id);
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());

        Category category = categoryService.findOrCreateCategory(product.getCategory().getName());
        existingProduct.setCategory(category);

        if (!imageFile.isEmpty()) {
            String imageUrl = productService.saveImage(imageFile);
            existingProduct.setImage(imageUrl);
        }

        productService.updateProduct(existingProduct);
        model.addAttribute("message", "Product updated successfully!");
        return "redirect:/admin";
    }

    @PostMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable Long id, Model model) {
        productService.deleteProduct(id);
        model.addAttribute("message", "Product deleted successfully!");
        return "redirect:/admin";
    }


    @GetMapping("/product/{productId}")
    public String getProductDetails(@PathVariable Long productId, Model model) {
        Product product = productService.getProductById(productId);
        if (product != null) {
            model.addAttribute("product", product);
            Category category = product.getCategory();
            if (category != null) {
                model.addAttribute("category", category);
            }
            return "product-details";
        } else {
            return "product-not-found";
        }
    }

    @GetMapping("/admin/orders")
    public String getAllOrders(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null || !loggedInUser.getRole().equals("admin")) {
            return "redirect:/login";
        }

        model.addAttribute("orders", orderService.getAllOrders());
        return "admin-orders";
    }

    @GetMapping("/admin/user/orders")
    public String getAdminUserOrders(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null || !loggedInUser.getRole().equals("admin")) {
            return "redirect:/login";
        }

        model.addAttribute("orders", orderService.getOrdersByUserId(loggedInUser.getId()));
        return "admin-user-orders";
    }

    @PostMapping("/admin/update-delivery-status/{orderId}")
    @ResponseBody
    public String updateDeliveryStatus(@PathVariable("orderId") Long orderId,
                                       @RequestParam("status") String status,
                                       @RequestParam("deliveryDate") String deliveryDateStr) {
        if (deliveryDateStr.isEmpty()) {
            // Handle empty delivery date
            return "Error: Delivery date is empty.";
        }

        Date deliveryDate = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            deliveryDate = dateFormat.parse(deliveryDateStr);
        } catch (ParseException e) {
            // Handle parsing exception
            e.printStackTrace();
            return "Error: Failed to parse delivery date.";
        }

        orderService.updateDeliveryStatus(orderId, status, deliveryDate);
        return "redirect:/admin";
    }
    @PostMapping("/admin/update-delivery/{orderId}")
    @ResponseBody
    public String updateDeliveryStatusAndDate(@PathVariable("orderId") Long orderId,
                                              @RequestParam("status") String status,
                                              @RequestParam("deliveryDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date deliveryDate) {
        orderService.updateDeliveryStatusAndDate(orderId, status, deliveryDate);
        return "redirect:/admin";
    }
    @PostMapping("/admin/delete-order/{orderId}")
    @ResponseBody
    public String deleteOrder(@PathVariable("orderId") Long orderId) {
        orderService.deleteOrder(orderId);
        return "redirect:/admin";
    }


}
