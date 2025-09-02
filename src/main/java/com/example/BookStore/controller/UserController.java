package com.example.BookStore.controller;

import com.example.BookStore.entity.Book;
import com.example.BookStore.entity.MyBookList;
import com.example.BookStore.entity.User;
import com.example.BookStore.service.BookService;
import com.example.BookStore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private final BookService bookService;

    private final UserService userService;

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

        // Если null → подставляем пустой список
        model.addAttribute("myBookTitles", myBookTitles != null ? myBookTitles : List.of());

        return "available_books";
    }

}
