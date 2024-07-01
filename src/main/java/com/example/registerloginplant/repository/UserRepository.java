package com.example.registerloginplant.repository;

import com.example.registerloginplant.entity.Order;
import com.example.registerloginplant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Integer> {
    User findByEmailAndPassword(String email, String password);
    User findByEmail(String email);
    void deleteById(long id);
}
