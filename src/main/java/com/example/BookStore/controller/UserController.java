package com.example.BookStore.controller;

import com.example.BookStore.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    private final BookService bookService;

    public UserController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/available_books")
    public String getAllBooks(Model model){
        model.addAttribute("books", bookService.getAllBooks());
        return "available_books";
    }

}
