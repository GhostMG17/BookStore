package com.example.BookStore.controller;

import com.example.BookStore.entity.book.Author;
import com.example.BookStore.service.AuthorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/admin/authors")
public class AdminAuthorController {
    private final AuthorService authorService;

    public AdminAuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public String listAuthors(Model model) {
        model.addAttribute("authors", authorService.getAllAuthors());
        return "admin/authors/list";
    }


    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("author", new Author());
        return "admin/authors/form";
    }

    @PostMapping("/create")
    public String createAuthor(@ModelAttribute("author") Author author,
                               @RequestParam("photoFile") MultipartFile photoFile) throws IOException {

        if (!photoFile.isEmpty()) {
            String uploadDir = "uploads/authors/";
            String fileName = author.getName().replaceAll("\\s+", "_") + "_" + photoFile.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = photoFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                author.setPhotoPath("/" + uploadDir + fileName); // путь для HTML
            }
        }

        authorService.saveAuthor(author);
        return "redirect:/admin/authors";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Author author = authorService.getAuthorById(id);
        model.addAttribute("author", author);
        model.addAttribute("books", author.getBooks());
        return "admin/authors/form";
    }

    @PostMapping("/edit/{id}")
    public String updateAuthor(@PathVariable("id") Long id,
                               @ModelAttribute("author") Author authorFromForm,
                               @RequestParam("photoFile") MultipartFile photoFile) throws IOException {

        Author existingAuthor = authorService.getAuthorById(id);
        existingAuthor.setName(authorFromForm.getName());
        existingAuthor.setBiography(authorFromForm.getBiography());
        existingAuthor.setBirthDate(authorFromForm.getBirthDate());

        if (!photoFile.isEmpty()) {
            String uploadDir = "uploads/authors/";
            String fileName = existingAuthor.getId() + "_" + photoFile.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = photoFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                existingAuthor.setPhotoPath("/" + uploadDir + fileName);
            }
        }

        authorService.saveAuthor(existingAuthor);
        return "redirect:/admin/authors";
    }


    @GetMapping("/delete/{id}")
    public String deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return "redirect:/admin/authors";
    }
}

