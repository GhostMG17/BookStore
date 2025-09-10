package com.example.BookStore.controller.CartController;

import com.example.BookStore.entity.book.Book;
import com.example.BookStore.entity.cart.Cart;
import com.example.BookStore.entity.cart.CartItem;
import com.example.BookStore.entity.cart.Order;
import com.example.BookStore.entity.cart.OrderItem;
import com.example.BookStore.entity.user.User;
import com.example.BookStore.repository.BookRepository;
import com.example.BookStore.repository.CartLogicRepository.OrderItemRepository;
import com.example.BookStore.repository.CartLogicRepository.OrderRepository;
import com.example.BookStore.repository.UserRepository;
import com.example.BookStore.service.CartServiceLogic.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public CartController(CartService cartService,
                          UserRepository userRepository,
                          BookRepository bookRepository,
                          OrderRepository orderRepository,
                          OrderItemRepository orderItemRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    // Показать корзину
    @GetMapping
    public String viewCart(Model model, Principal principal) {
        // Получаем пользователя по email из сессии
        User user = userRepository.findByEmail(principal.getName());

        // Получаем корзину пользователя
        Cart cart = cartService.getCart(user.getId());

        double totalPrice = cart.getItems().stream()
                .mapToDouble(i -> i.getBook().getPrice() * i.getQuantity())
                .sum();

        model.addAttribute("cart", cart);
        model.addAttribute("totalPrice", totalPrice);
        return "cart/view"; // Thymeleaf шаблон
    }

    // Добавить книгу в корзину
    @PostMapping("/add")
    public String addToCart(@RequestParam Long bookId,
                            @RequestParam(defaultValue = "1") int quantity,
                            Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        cartService.addToCart(user.getId(), book, quantity);
        return "redirect:/cart";
    }

    // Удалить книгу из корзины
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long cartItemId, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        cartService.removeFromCart(user.getId(), cartItemId);
        return "redirect:/cart";
    }

    // Очистить корзину
    @PostMapping("/clear")
    public String clearCart(Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        cartService.clearCart(user.getId());
        return "redirect:/cart";
    }



    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(Principal principal) {
        // 1. Получаем пользователя
        User user = userRepository.findByEmail(principal.getName());

        // 2. Получаем корзину пользователя
        Cart cart = cartService.getCartByUser(user);
        if (cart.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body("Корзина пуста");
        }

        // 3. Создаём заказ
        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(cart.getTotalAmount());
        order.setStatus(Order.OrderStatus.NEW);
        orderRepository.save(order);

        // 4. Копируем все CartItem в OrderItem
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getBook().getPrice());
            orderItemRepository.save(orderItem);
        }

        // 5. Очистка корзины
        cartService.clearCart(cart);

        return ResponseEntity.ok("Заказ успешно оформлен!");
    }

}
