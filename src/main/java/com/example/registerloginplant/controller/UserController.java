package com.example.registerloginplant.controller;

import com.example.registerloginplant.entity.*;
import com.example.registerloginplant.service.CategoryService;
import com.example.registerloginplant.service.OrderService;
import com.example.registerloginplant.service.ProductService;
import com.example.registerloginplant.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    OrderService orderService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {

        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, @RequestParam("role") String role, Model model) {
        if (!isPasswordValid(user.getPassword())) {
            model.addAttribute("passwordError", "Password requirements not met.");
            return "register";
        }

        // Validate email format
        if (!isEmailValid(user.getEmail())) {
            model.addAttribute("emailError", "Email ID is not valid.");
            return "register";
        }
        // Check if user with the same email already exists
        User existingUser = userService.findByEmail(user.getEmail());
        if (existingUser != null) {
            // Display appropriate message based on user's role
            if (existingUser.getRole().equals("admin")) {
                model.addAttribute("message", "Admin with this email already exists.");
            } else {
                model.addAttribute("message", "User with this email already exists.");
            }
            return "register";
        }

        user.setRole(role);
        userService.registerUser(user);
        model.addAttribute("user", new User());
        model.addAttribute("message", "User registered successfully!");
        return "redirect:/login";
    }
    private boolean isPasswordValid(String password) {
        // Password should contain at least 8 characters, one letter, one number, and one special character
        return password.length() >= 8 &&
                password.matches(".*[a-zA-Z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

    }

    private boolean isEmailValid(String email) {
        // Email should match a valid format
        return email.matches("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("login", new Login());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@ModelAttribute Login login, Model model, HttpSession session) {
        String email = login.getEmail();
        String password = login.getPassword();

        // Check if the user exists with the provided email and password
        User user = userService.login(email, password);

        if (user != null) {
            if (user.getRole().equals("admin")) {
                // Redirect to admin home if user is admin
                session.setAttribute("loggedInUser", user);
                return "redirect:/admin";
            } else {
                // Redirect to user home if user is not admin
                session.setAttribute("loggedInUser", user);
                return "redirect:/home";
            }
        } else {
            // User not found or invalid credentials
            model.addAttribute("error", "Invalid email or password.");
            return "login";
        }
    }

    @GetMapping("/home")
    public String userHome(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null || !loggedInUser.getRole().equals("user")) {
            return "redirect:/login";
        }

        model.addAttribute("user", loggedInUser);
        return "home";
    }

    @GetMapping("/about")
    public String showAboutPage() {
        return "about"; // This resolves to about.html
    }

    @GetMapping("/orders")
    public String getUserOrders(Model model, HttpSession session) {
        // Get the logged-in user from the session
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        // Check if the user is logged in
        if (loggedInUser == null) {
            // If not logged in, redirect to the login page
            return "redirect:/login";
        }

        // Retrieve orders for the logged-in user
        List<Order> userOrders = orderService.getOrdersByUserId(loggedInUser.getId());

        // Add user orders to the model
        model.addAttribute("userOrders", userOrders);

        // Return the view name for user orders
        return "user-orders";
    }


    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("passwordResetRequest", new PasswordResetRequest());
        return "forgot-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@ModelAttribute("passwordResetRequest") PasswordResetRequest passwordResetRequest,
                                BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            // If there are validation errors, return to the form with error messages
            return "forgot-password";
        }
        String email = passwordResetRequest.getEmail();
        String newPassword = passwordResetRequest.getNewPassword();
        String confirmPassword = passwordResetRequest.getConfirmPassword();

        // Validate input fields
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "forgot-password";
        }
        // Validate password requirements
        if (!isPasswordValid(newPassword)) {
            model.addAttribute("error", "Password requirements not met.");
            return "forgot-password";
        }

        // Check if the user with provided email exists
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("error", "Email not registered.");
            return "forgot-password";
        }

        // Update user's password
        user.setPassword(newPassword);
        userService.updateUser(user);

        model.addAttribute("message", "Password changed successfully. Please login with your new password.");
        return "login";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/succulents")
    public String getSucculents(Model model) {
        List<Product> succulents = productService.getProductsByCategory("succulents");
        model.addAttribute("products", succulents);
        return "succulents";
    }

    @GetMapping("/cactus")
    public String getCactus(Model model) {
        List<Product> cactus = productService.getProductsByCategory("cactus");
        model.addAttribute("products", cactus);
        return "cactus";
    }

    @GetMapping("/miniatures")
    public String getMiniatures(Model model) {

        List<Product> miniatures = productService.getProductsByCategory("miniatures");
        model.addAttribute("products", miniatures);
        return "miniatures";
    }
    @GetMapping("/pots")
    public String getPots(Model model) {
        List<Product> pots = productService.getProductsByCategory("pots");
        model.addAttribute("products", pots);
        return "pots";
    }

    @GetMapping("/seeds")
    public String getSeeds(Model model) {
        List<Product> seeds = productService.getProductsByCategory("seeds");
        model.addAttribute("products", seeds);
        return "seeds";
    }

    @GetMapping("/tools")
    public String getTools(Model model) {
        List<Product> tools = productService.getProductsByCategory("tools");
        model.addAttribute("products", tools);
        return "tools";
    }

    @GetMapping("/users")
    public String getAllUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @PostMapping("/admin/delete-user/{id}")
    public String deleteUser(@PathVariable("id") Long userId) {
        userService.deleteUser(userId);
        return "redirect:/admin";
    }
    
}


