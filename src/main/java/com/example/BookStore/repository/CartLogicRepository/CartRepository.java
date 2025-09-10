package com.example.BookStore.repository.CartLogicRepository;

import com.example.BookStore.entity.cart.Cart;
import com.example.BookStore.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}