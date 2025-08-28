package com.example.BookStore.service;

import com.example.BookStore.entity.MyBookList;
import com.example.BookStore.repository.MyBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyBookListService {
    @Autowired
    private MyBookRepository mybook;

    public void saveMyBooks(MyBookList myBookList) {
        mybook.save(myBookList);
    }

    public List<MyBookList> getMyBookList() {
        return mybook.findAll();
    }

    public void deleteById(Long id) {
        mybook.deleteById(id);
    }

}
