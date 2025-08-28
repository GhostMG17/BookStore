package com.example.BookStore.controller;

import com.example.BookStore.entity.Book;
import com.example.BookStore.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final BookService bookService;

    public AdminController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/available_books_for_admin")
    public String getAllBooks(Model model){
        model.addAttribute("books", bookService.getAllBooks());
        return "admin/booking_management";
    }

    @GetMapping("/book_register")
    public String showBookRegister(Model model){
        model.addAttribute("book", new Book());
        return "book_register";
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
        return "edit_book";
    }


//    @PostMapping("/update_book/{id}")
//    public String update_book(@ModelAttribute Book book){
//        bookService.updateBook(book);
//        return "redirect:/available_books";
//    }

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

    @GetMapping("/delete_book/{id}")
    public String delete_book(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return "redirect:/available_books";
    }

}
