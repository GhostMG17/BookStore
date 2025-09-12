package com.example.BookStore.controller;

import com.example.BookStore.entity.book.Book;
import com.example.BookStore.entity.book.MyBookList;
import com.example.BookStore.entity.user.User;
import com.example.BookStore.service.BookService;
import com.example.BookStore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private final BookService bookService;

    private final UserService userService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UserController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    @GetMapping("/available_books")
    public String availableBooks(Model model, Principal principal) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);

        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<String> myBookTitles = user.getMyBooks().stream()
                .map(MyBookList::getTitle)
                .toList();

        model.addAttribute("myBookTitles", myBookTitles != null ? myBookTitles : List.of());

        return "available_books";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal){
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user",user);
        return "profile/profile_page";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model, Principal principal){
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user",user);
        return "profile/edit_profile";
    }

    @PostMapping("/profile/update_profile/{id}")
    public String updateProfile(@PathVariable("id") Long id,
                                @ModelAttribute("user") User userFromForm,
                                @RequestParam("avatarFile")MultipartFile avatarFile)throws IOException {
        User existingUser = userService.getById(id);
        existingUser.setUsername(userFromForm.getUsername());
        existingUser.setFirstName(userFromForm.getFirstName());
        existingUser.setLastName(userFromForm.getLastName());
        existingUser.setEmail(userFromForm.getEmail());
        existingUser.setPhone(userFromForm.getPhone());

        if (!avatarFile.isEmpty()) {
            // Папка, куда сохраняем
            String uploadDir = "uploads/avatars/";
            String fileName = existingUser.getId() + "_" + avatarFile.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = avatarFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                existingUser.setAvatar("/" + uploadDir + fileName); // путь, который отдадим в HTML
            }
        }

        userService.updateUser(existingUser);
        return "redirect:/profile";
    }

    @GetMapping("/profile/change_password")
    public String changePasswordPage() {
        return "profile/change_password";
    }

    @PostMapping("/profile/change_password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("message", "Old Password Doesn't Match");
            return "redirect:/profile/change_password";
        }

        if(!newPassword.equals(confirmPassword)){
            redirectAttributes.addFlashAttribute("message", "New Password Doesn't Match");
            return "redirect:/profile/change_password";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userService.saveUser(user);

        redirectAttributes.addFlashAttribute("message", "Password Changed Successfully");
        return "redirect:/profile";
    }
}
