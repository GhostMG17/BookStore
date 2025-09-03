package com.example.BookStore.controller;

import com.example.BookStore.dto.UserDto;
import com.example.BookStore.entity.Book;
import com.example.BookStore.entity.User;
import com.example.BookStore.service.BookService;
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
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final BookService bookService;
    private final UserService userService;

    public AdminController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
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
    public String getAllBooks(Model model){
        model.addAttribute("books", bookService.getAllBooks());
        return "admin/booking_management";
    }

    @GetMapping("/book_register")
    public String showBookRegister(Model model){
        model.addAttribute("book", new Book());
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
        bookService.save(book);
        return "redirect:/available_books";
    }


    @GetMapping("/edit_book/{id}")
    public String edit_book(@PathVariable("id") Long id, Model model){
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
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
        return "redirect:/available_books";
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
}
