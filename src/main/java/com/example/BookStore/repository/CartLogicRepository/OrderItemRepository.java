package com.example.BookStore.repository.CartLogicRepository;

import com.example.BookStore.entity.cart.Order;
import com.example.BookStore.entity.cart.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
}

