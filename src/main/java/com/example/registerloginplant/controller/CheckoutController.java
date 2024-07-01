package com.example.registerloginplant.controller;


import com.example.registerloginplant.entity.Address;
import com.example.registerloginplant.entity.Cart;
import com.example.registerloginplant.entity.Order;
import com.example.registerloginplant.entity.User;
import com.example.registerloginplant.service.CartService;
import com.example.registerloginplant.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;

    @Autowired
    public CheckoutController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping("/address")
    public String showAddressForm() {
        return "address";
    }

    @PostMapping("/payment")
    public String proceedToPayment(@RequestParam("name") String name, @RequestParam("address") String address,
                                   @RequestParam("city") String city, @RequestParam("state") String state,
                                   @RequestParam("zip") String zip, HttpSession session, Model model) {
        Address deliveryAddress = new Address();
        deliveryAddress.setName(name);
        deliveryAddress.setAddress(address);
        deliveryAddress.setCity(city);
        deliveryAddress.setState(state);
        deliveryAddress.setZip(zip);

        // Save the address details in the session
        session.setAttribute("deliveryAddress", deliveryAddress);

        // Retrieve cart total
        double total = cartService.getCartTotal(session);

        // Add details to the model
        model.addAttribute("name", name);
        model.addAttribute("address", address);
        model.addAttribute("city", city);
        model.addAttribute("state", state);
        model.addAttribute("zip", zip);
        model.addAttribute("total", total);

        // Render Razorpay payment page
        return "payment";
    }

    // CheckoutController.java
    @PostMapping("/complete")
    public String completeCheckout(@RequestParam("razorpay_payment_id") String paymentId,
                                   HttpSession session, Model model) {
        Address address = (Address) session.getAttribute("deliveryAddress");
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (address == null) {
            return "redirect:/checkout/address";
        }
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        // Retrieve cart from session
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.getItems().isEmpty()) {
            model.addAttribute("error", "Cart is empty");
            return "error"; // Handle empty cart scenario
        }

        // Create order and save it
        Order order = orderService.createOrderFromCart(loggedInUser, address, paymentId, session);

        model.addAttribute("message", "Payment successful and order placed!");
        model.addAttribute("orderId", order.getId());

        return "orderConfirmation";
    }

}
