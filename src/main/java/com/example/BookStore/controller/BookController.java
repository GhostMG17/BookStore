package com.example.BookStore.controller;

import com.example.BookStore.entity.book.Book;
import com.example.BookStore.entity.book.MyBookList;
import com.example.BookStore.entity.user.User;
import com.example.BookStore.service.BookService;
import com.example.BookStore.service.MyBookListService;
import com.example.BookStore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class BookController {
    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private MyBookListService myBookListService;

    @GetMapping("/")
    public String home(){
        return "home";
    }

    @GetMapping("/book_download/{id}")
    public ResponseEntity<Resource> downloadBook(@PathVariable Long id) throws IOException {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getFilePath() == null) {
            throw new RuntimeException("Файл для этой книги не найден");
        }

        Path path = Paths.get(book.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        String contentType = Files.probeContentType(path);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path.getFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/my_books")
    public String my_books(Model model, Principal principal){
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<String> myBookTitles = user.getMyBooks().stream()
                        .map(MyBookList::getTitle)
                                .collect(Collectors.toSet());

        model.addAttribute("books", user.getMyBooks());
        model.addAttribute("myBookTitles", myBookTitles);
        return "my_books";
    }

    @GetMapping("/deleteMyList/{id}")
    public String deleteMyBook(@PathVariable("id") Long id, Principal principal){
        myBookListService.deleteById(id);
        return "redirect:/my_books";
    }

    @PostMapping("/save")
    public String addBookInAvailableBooks(@ModelAttribute Book book){
        bookService.save(book);
        return "redirect:/available_books";
    }

    @RequestMapping("/mylist/{id}")
    public String getMyList(@PathVariable("id") Long id, Principal principal) {
        Book book = bookService.getBookById(id);

        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Проверяем, есть ли книга в списке пользователя
        boolean alreadyAdded = user.getMyBooks().stream()
                .anyMatch(b -> b.getTitle().equals(book.getTitle())
                        && b.getAuthor().equals(book.getAuthor())
                        && Objects.equals(b.getPrice(), book.getPrice())
                        && b.getYear() == book.getYear());

        if (!alreadyAdded) {
            MyBookList myBookList = new MyBookList(book.getTitle(), book.getAuthor(), book.getPrice(), book.getYear());
            myBookListService.saveMyBooks(myBookList);

            user.getMyBooks().add(myBookList);
            userService.saveUser(user);
        }

        return "redirect:/my_books";
    }





}
