package com.example.registerloginplant.controller;


import com.example.registerloginplant.entity.Cart;
import com.example.registerloginplant.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId, HttpSession session) {
        cartService.addToCart(productId, session);
        return "redirect:/cart/view";
    }

    @PostMapping("/remove")
    public String removeFromCartAndSave(@RequestParam("productId") Long productId, HttpSession session) {
        cartService.removeFromCartAndSave(productId, session);
        return "redirect:/cart/view";
    }

    @GetMapping("/view")
    public String viewCart(Model model, HttpSession session) {
        Cart cart = cartService.getCart(session);
        model.addAttribute("cart", cart);
        model.addAttribute("total", cart.getTotal());
        return "cart";
    }

}
