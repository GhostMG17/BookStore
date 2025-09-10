package com.example.BookStore.repository.CartLogicRepository;


import com.example.BookStore.entity.cart.Order;
import com.example.BookStore.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);
}
