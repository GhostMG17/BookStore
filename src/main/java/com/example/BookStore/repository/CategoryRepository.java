package com.example.BookStore.repository;

import com.example.BookStore.entity.book.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
