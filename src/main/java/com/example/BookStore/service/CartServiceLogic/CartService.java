package com.example.BookStore.service.CartServiceLogic;


import com.example.BookStore.entity.book.Book;
import com.example.BookStore.entity.cart.Cart;
import com.example.BookStore.entity.user.User;

public interface CartService {
    Cart getCart(Long userId);
    Cart addToCart(Long userId, Book book, int quantity);
    Cart removeFromCart(Long userId, Long cartItemId);
    void clearCart(Long userId);
    Cart getCartByUser(User user);
    void clearCart(Cart cart);
    double getTotalAmount(Cart cart); // сумма всех товаров

}
