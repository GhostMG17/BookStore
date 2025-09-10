package com.example.BookStore.service.CartServiceLogic;


import com.example.BookStore.entity.book.Book;
import com.example.BookStore.entity.cart.Cart;
import com.example.BookStore.entity.cart.CartItem;
import com.example.BookStore.entity.user.User;
import com.example.BookStore.repository.*;
import com.example.BookStore.repository.CartLogicRepository.CartItemRepository;
import com.example.BookStore.repository.CartLogicRepository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Cart getCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    @Override
    public Cart addToCart(Long userId, Book book, int quantity) {
        Cart cart = getCart(userId);

        // проверяем, есть ли уже книга в корзине
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getBook().getId().equals(book.getId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setBook(book);
            newItem.setQuantity(quantity);
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @Override
    public Cart removeFromCart(Long userId, Long cartItemId) {
        Cart cart = getCart(userId);

        cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        cartItemRepository.deleteById(cartItemId);

        return cartRepository.save(cart);
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = getCart(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    public Cart getCartByUser(User user) {
        return null;
    }

    @Override
    public void clearCart(Cart cart) {

    }

    @Override
    public double getTotalAmount(Cart cart) {
        return 0;
    }
}
