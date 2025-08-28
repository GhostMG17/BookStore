package com.example.BookStore.controller;

import com.example.BookStore.entity.Book;
import com.example.BookStore.entity.MyBookList;
import com.example.BookStore.service.BookService;
import com.example.BookStore.service.MyBookListService;
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
import java.util.List;

@Controller
public class BookController {
    @Autowired
    private BookService bookService;

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
    public String my_books(Model model){
        List<MyBookList> list = myBookListService.getMyBookList();
        model.addAttribute("books", list);
        return "my_books";
    }

    @GetMapping("/deleteMyList/{id}")
    public String deleteMyBook(@PathVariable("id") Long id){
        myBookListService.deleteById(id);
        return "redirect:/my_books";
    }

    @PostMapping("/save")
    public String addBookInAvailableBooks(@ModelAttribute Book book){
        bookService.save(book);
        return "redirect:/available_books";
    }

    @RequestMapping("/mylist/{id}")
    public String getMyList(@PathVariable("id") Long id){
        Book book = bookService.getBookById(id);
        MyBookList myBookList = new MyBookList(book.getTitle(),book.getAuthor(),book.getPrice(),book.getYear());
        myBookListService.saveMyBooks(myBookList);
        return "redirect:/my_books";
    }




}
