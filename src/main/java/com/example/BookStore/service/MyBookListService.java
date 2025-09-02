package com.example.BookStore.service;

import com.example.BookStore.entity.MyBookList;
import com.example.BookStore.entity.User;
import com.example.BookStore.repository.MyBookRepository;
import com.example.BookStore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyBookListService {
    @Autowired
    private MyBookRepository myBookRepository;

    @Autowired
    private UserRepository userRepository;

    public void saveMyBooks(MyBookList myBookList) {
        myBookRepository.save(myBookList);
    }

    public List<MyBookList> getMyBookList() {
        return myBookRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        MyBookList book = myBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Remove the book from all users
        for (User user : userRepository.findAll()) {
            if (user.getMyBooks().remove(book)) {
                userRepository.save(user);
            }
        }

        // Now it's safe to delete the book itself
        myBookRepository.delete(book);
    }

    public MyBookList getById(Long id) {
        return myBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

}
