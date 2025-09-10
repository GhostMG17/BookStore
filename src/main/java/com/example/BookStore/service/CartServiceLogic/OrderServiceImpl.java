package com.example.BookStore.service.CartServiceLogic;


import com.example.BookStore.entity.cart.Cart;
import com.example.BookStore.entity.cart.CartItem;
import com.example.BookStore.entity.cart.Order;
import com.example.BookStore.entity.cart.OrderItem;
import com.example.BookStore.entity.user.User;
import com.example.BookStore.repository.*;
import com.example.BookStore.repository.CartLogicRepository.CartRepository;
import com.example.BookStore.repository.CartLogicRepository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(CartRepository cartRepository,
                            OrderRepository orderRepository,
                            UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Order createOrder(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Корзина пуста");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.OrderStatus.NEW);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        double totalPrice = 0;

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getBook().getPrice());
            order.getItems().add(orderItem);

            totalPrice += cartItem.getBook().getPrice() * cartItem.getQuantity();
        }

        order.setTotalPrice(totalPrice);

        // Очистка корзины
        cart.getItems().clear();
        cartRepository.save(cart);

        return orderRepository.save(order);
    }
}

