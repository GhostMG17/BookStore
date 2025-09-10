package com.example.BookStore.service.CartServiceLogic;


import com.example.BookStore.entity.cart.Order;

public interface OrderService {
    Order createOrder(Long userId);
}
