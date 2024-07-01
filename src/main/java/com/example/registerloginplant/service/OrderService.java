package com.example.registerloginplant.service;

import com.example.registerloginplant.entity.Address;
import com.example.registerloginplant.entity.Cart;
import com.example.registerloginplant.entity.Order;
import com.example.registerloginplant.entity.User;
import com.example.registerloginplant.repository.OrderRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;

    @Autowired
    public OrderService(OrderRepository orderRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
    }

    public List<Order> getOrdersByUserId(long userId) {
        return orderRepository.findByUserId(userId);
    }

    public void updateDeliveryStatus(Long orderId, String status, Date deliveryDate) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setDeliveryStatus(status);
            order.setDeliveryDate(deliveryDate);
            orderRepository.save(order);
        }
    }

    public Order createOrderFromCart(User user, Address address, String paymentId, HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setAddress(address);
        order.setItems(cart.getItems());
        order.setPaymentId(paymentId);
        order.setTotal(cart.getTotal());
        order.setUser(user);

        // Set default delivery status as "Pending"
        order.setDeliveryStatus("Pending");

        // Increment delivery date by 2 days
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        order.setDeliveryDate(calendar.getTime());

        // Save order to the database
        orderRepository.save(order);

        // Clear the cart after order creation
        session.setAttribute("cart", cart);

        return order;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Optional method for creating orders with a logged-in user
    public Order createOrder(User user, Address address, String paymentId, HttpSession session) {
        // Retrieve cart from session
        Cart cart = cartService.getCartFromSession(session);

        // Check if the cart is empty
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Create a new order
        Order order = new Order();
        order.setAddress(address);
        order.setItems(cart.getItems());
        order.setPaymentId(paymentId);
        order.setTotal(cart.getTotal());
        order.setUser(user);

        // Set default delivery status as "Pending"
        order.setDeliveryStatus("Pending");

        // Increment delivery date by 2 days
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        order.setDeliveryDate(calendar.getTime());

        // Save order to the database
        orderRepository.save(order);

        return order;
    }

    public void updateDeliveryStatusAndDate(Long orderId, String status, Date deliveryDate) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setDeliveryStatus(status);
        order.setDeliveryDate(deliveryDate);
        orderRepository.save(order);
    }
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

}
