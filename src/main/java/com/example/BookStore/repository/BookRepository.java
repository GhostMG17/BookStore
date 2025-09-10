package com.example.BookStore.repository;

import com.example.BookStore.entity.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {
    @Query("SELECT COUNT(b) FROM Book b")
    long countBooks();

    @Query("SELECT b FROM Book b ORDER BY b.id DESC")
    List<Book> findRecentBooks(Pageable pageable);

    List<Book> findByCategoryId(Long categoryId);
}
