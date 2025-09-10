package com.example.BookStore.repository.CartLogicRepository;

import com.example.BookStore.entity.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}