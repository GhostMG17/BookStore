package com.example.BookStore.entity;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;

@Entity
@Table(name = "MyBooks")
public class MyBookList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private String author;
    private Double price;
    private int year;

    @ManyToMany(mappedBy = "myBooks")
    private Set<User> users = new HashSet<>();


    public MyBookList(String title, String author, Double price,  int year) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.year = year;
    }

    public MyBookList() {

    }


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
