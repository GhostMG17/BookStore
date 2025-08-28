package com.example.BookStore.controller;

import com.example.BookStore.entity.Book;
import com.example.BookStore.entity.MyBookList;
import com.example.BookStore.service.BookService;
import com.example.BookStore.service.MyBookListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/book_register")
    public String showBookRegister(Model model){
        model.addAttribute("book", new Book());
        return "book_register";
    }

    @PostMapping("/book_add")
    public String addBook(Book book){
        bookService.save(book);
        return "redirect:/available_books";
    }

    @GetMapping("/available_books")
    public String getAllBooks(Model model){
        model.addAttribute("books", bookService.getAllBooks());
        return "available_books";
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

    @GetMapping("/edit_book/{id}")
    public String edit_book(@PathVariable("id") Long id, Model model){
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        return "edit_book";
    }

    @PostMapping("/update_book/{id}")
    public String update_book(@ModelAttribute Book book){
        bookService.updateBook(book);
        return "redirect:/available_books";
    }

    @GetMapping("/delete_book/{id}")
    public String delete_book(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return "redirect:/available_books";
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
