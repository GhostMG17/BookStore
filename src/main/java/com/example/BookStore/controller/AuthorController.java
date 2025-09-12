package com.example.BookStore.controller;


import com.example.BookStore.entity.book.Author;
import com.example.BookStore.repository.AuthorRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorRepository authorRepository;

    public AuthorController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    // Список всех авторов
    @GetMapping
    public String listAuthors(Model model) {
        List<Author> authors = authorRepository.findAll();
        model.addAttribute("authors", authors);
        return "authors/list";
    }

    // Страница конкретного автора
    @GetMapping("/{id}")
    public String authorDetails(@PathVariable Long id, Model model) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        model.addAttribute("author", author);
        return "authors/details";
    }
}
