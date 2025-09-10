package com.example.BookStore.entity.user;
import com.example.BookStore.entity.book.MyBookList;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @Column(nullable = false,unique = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "First name is required")
    @Size(max = 30, message = "First name must not exceed 30 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 30, message = "Last name must not exceed 30 characters")
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9\\-+]{9,15}$", message = "Invalid phone number")
    private String phone;

    @Column(name = "avatar")
    private String avatar;

    @Enumerated(EnumType.STRING)
    private Role role; // ADMIN или USER

    @Column(nullable = false)
    private boolean enabled = false;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;


    @ManyToMany
    @JoinTable(
            name = "user_books",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )

    private Set<MyBookList> myBooks = new HashSet<>();

    public User(Long id, String username, String password, Role role, String firstName, String lastName, String phone,String avatar ,String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.avatar = avatar;
        this.email = email;
    }

    public String getAvatar() {return avatar;}

    public void setAvatar(String avatar) {this.avatar = avatar;}

    public User() {}

    public enum Role {ADMIN, USER}

    public LocalDateTime getCreatedAt() {return createdAt;}

    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}

    public LocalDateTime getUpdatedAt() {return updatedAt;}

    public void setUpdatedAt(LocalDateTime updatedAt) {this.updatedAt = updatedAt;}

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getUsername() {return username;}

    public void setUsername(String username) {this.username = username;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public Role getRole() {return role;}

    public void setRole(Role role) {this.role = role;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getFirstName() {return firstName;}

    public void setFirstName(String firstName) {this.firstName = firstName;}

    public String getLastName() {return lastName;}

    public void setLastName(String lastName) {this.lastName = lastName;}

    public String getPhone() {return phone;}

    public void setPhone(String phone) {this.phone = phone;}

    public Set<MyBookList> getMyBooks() {return myBooks;}

    public void setMyBooks(Set<MyBookList> myBooks) {this.myBooks = myBooks;}

    public boolean isEnabled() {return enabled;}

    public void setEnabled(boolean enabled) {this.enabled = enabled;}
}
