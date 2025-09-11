package com.example.BookStore.controller;

import com.example.BookStore.dto.UserDto;
import com.example.BookStore.entity.book.Book;
import com.example.BookStore.entity.book.Category;
import com.example.BookStore.entity.cart.Order;
import com.example.BookStore.entity.user.User;
import com.example.BookStore.repository.CartLogicRepository.OrderRepository;
import com.example.BookStore.repository.UserRepository;
import com.example.BookStore.service.BookService;
import com.example.BookStore.service.CartServiceLogic.OrderService;
import com.example.BookStore.service.CategoryService;
import com.example.BookStore.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final BookService bookService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderService orderService;


    public AdminController(BookService bookService,
                           UserService userService,
                           CategoryService categoryService,
                           OrderRepository orderRepository,
                           UserRepository userRepository,
                           OrderService orderService) {
        this.bookService = bookService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderService = orderService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long bookCount = bookService.countBooks();
        long userCount = userService.countUsers();
        List<Book> recentBooks = bookService.findRecentBooks();

        model.addAttribute("bookCount", bookCount);
        model.addAttribute("userCount", userCount);
        model.addAttribute("recentBooks", recentBooks);

        return "admin/dashboard"; // создадим этот шаблон
    }


    @GetMapping("/available_books_for_admin")
    public String getAllBooks(@RequestParam(required = false) Long categoryId, Model model) {
        List<Category> categories = categoryService.getAllCategories();
        List<Book> books;

        if (categoryId != null) {
            books = bookService.getBooksByCategoryId(categoryId);
        } else {
            books = bookService.getAllBooks();
        }

        model.addAttribute("books", books);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategoryId", categoryId);

        return "admin/booking_management";
    }


    @GetMapping("/book_register")
    public String showBookRegister(Model model){
        model.addAttribute("book", new Book());
        model.addAttribute("categories",categoryService.getAllCategories());
        return "admin/book_register";
    }


    @GetMapping("/users_management")
    public String getAllUsers(Model model){
        model.addAttribute("users",userService.getAllUsers());
        return "admin/users_management";
    }


    @GetMapping("/user/{id}")
    @ResponseBody
    public UserDto getUser(@PathVariable Long id){
        User user = userService.findById(id);
        return new UserDto(user);
    }


    @PostMapping("/book_add")
    public String addBook(@ModelAttribute Book book,
                          @RequestParam("categoryId") Long categoryId,
                          @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Файл книги не выбран!");
        }

        String uploadDir = "uploads/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());
        book.setFilePath(filePath.toString());

        if (categoryId != null) {
            Category category = categoryService.getCategoryById(categoryId);
            book.setCategory(category);
        }
        bookService.save(book);
        return "redirect:/admin/available_books_for_admin";
    }


    @GetMapping("/edit_book/{id}")
    public String edit_book(@PathVariable("id") Long id, Model model){
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        model.addAttribute("categories",categoryService.getAllCategories());
        return "admin/edit_book";
    }


    @PostMapping("/update_book/{id}")
    public String updateBook(@PathVariable Long id,
                             @ModelAttribute Book book,
                             @RequestParam("file") MultipartFile file) throws IOException {

        Book existingBook = bookService.getBookById(id);
        // Обновляем данные книги
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setPrice(book.getPrice());
        existingBook.setYear(book.getYear());
        existingBook.setCategory(book.getCategory());

        // Если пришёл новый файл, сохраняем его
        if (!file.isEmpty()) {
            String uploadDir = "uploads/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());

            existingBook.setFilePath(filePath.toString());
        }
        bookService.updateBook(existingBook);
        return "redirect:/admin/available_books_for_admin";
    }


    @PostMapping("/update_user/{id}")
    public String updateUser(@PathVariable("id") Long id,
                             @ModelAttribute("user") User userFromForm,
                             RedirectAttributes redirectAttributes) {

        User existingUser = userService.getById(id);
        if (existingUser == null) {
            redirectAttributes.addFlashAttribute("error", "Пользователь не найден!");
            return "redirect:/admin/users_management";
        }

        // Обновляем поля
        existingUser.setUsername(userFromForm.getUsername());
        existingUser.setFirstName(userFromForm.getFirstName());
        existingUser.setLastName(userFromForm.getLastName());

        userService.updateUser(existingUser);
        redirectAttributes.addFlashAttribute("success", "Пользователь обновлён!");
        return "redirect:/admin/users_management";
    }


    @GetMapping("/delete_book/{id}")
    public String delete_book(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return "redirect:/available_books";
    }


    @GetMapping("/delete_user/{id}")
    public String delete_user(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User user = userService.findById(id);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Пользователь не найден!");
            return "redirect:/admin/users_management";
        }

        if (user.getRole() == User.Role.ADMIN) {
            redirectAttributes.addFlashAttribute("error","Нельзя удалить администратора!");
            return "redirect:/admin/users_management?error=admin";
        }
        userService.deleteUserById(id);
        redirectAttributes.addFlashAttribute("success", "Пользователь успешно удалён.");
        return "redirect:/admin/users_management?success=true";
    }


    @GetMapping("/orders")
    public String orders(Model model) {
        List<Order> orders = orderRepository.findAll();
        model.addAttribute("orders", orders);
        return "admin/orders";
    }


    @GetMapping("/orders/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        model.addAttribute("order", order);
        model.addAttribute("statuses", Order.OrderStatus.values());
        return "admin/order_details";
    }


//    @PostMapping("/{id}/status")
//    public String updateStatus(@PathVariable Long id,
//                               @RequestParam("status") Order.OrderStatus status) {
//        Order order = orderRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//        order.setStatus(status);
//        orderRepository.save(order);
//
//        return "redirect:/admin/orders";
//    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam("status") Order.OrderStatus status) {
        // Получаем заказ
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Используем сервис, чтобы обновить статус и отправить уведомление
        orderService.updateOrderStatus(order, status);
        return "redirect:/admin/orders";
    }


}
