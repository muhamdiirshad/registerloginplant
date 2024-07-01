package com.example.registerloginplant.repository;


import com.example.registerloginplant.entity.Order;
import com.example.registerloginplant.entity.Product;
import com.example.registerloginplant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
        List<Order> findByUserId(Long userId);
    }


