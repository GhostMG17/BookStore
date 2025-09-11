package com.example.BookStore.controller.CartController;

import com.example.BookStore.entity.cart.Order;
import com.example.BookStore.entity.user.User;
import com.example.BookStore.repository.CartLogicRepository.OrderItemRepository;
import com.example.BookStore.repository.CartLogicRepository.OrderRepository;
import com.example.BookStore.repository.UserRepository;
import com.example.BookStore.service.CartServiceLogic.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderController(OrderService orderService,
                           UserRepository userRepository,
                           OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @PostMapping("/orders/create")
    public String createOrder(@RequestParam Long userId) {
        Order order = orderService.createOrder(userId);

        return "redirect:/orders/history?userId=" + userId;
    }

    @GetMapping("/orders/history")
    public String orderHistory(Model model,Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        List<Order> orders = orderRepository.findByUser(user);
        model.addAttribute("orders", orders);
        return "orders/history"; // Thymeleaf-шаблон
    }


    @GetMapping("/orders/{orderId}")
    public String orderDetails(@PathVariable Long orderId, Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Проверка, что заказ принадлежит текущему пользователю
        if (!order.getUser().getId().equals(user.getId())) {
            return "redirect:/orders/history"; // или страница ошибки
        }

        model.addAttribute("order", order);
        model.addAttribute("items", orderItemRepository.findByOrder(order));
        return "orders/details"; // Thymeleaf шаблон
    }


    @PostMapping("/orders/cancel")
    public String cancelOrder(@RequestParam Long orderId, Principal principal, Model model) {
        User user = userRepository.findByEmail(principal.getName());
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != Order.OrderStatus.NEW) {
            model.addAttribute("error", "Заказ нельзя отменить, он уже в обработке или выполнен");
            return "orders/history";
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);

        return "redirect:/orders/history?userId=" + user.getId();
    }

}
