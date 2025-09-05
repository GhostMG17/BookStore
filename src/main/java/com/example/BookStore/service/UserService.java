package com.example.BookStore.service;

import com.example.BookStore.entity.User;
import com.example.BookStore.entity.VerificationToken;
import com.example.BookStore.repository.UserRepository;
import com.example.BookStore.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    public User registerUser(User user) {
        // Шифруем пароль
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.USER);
        user.setEnabled(false); // по умолчанию не активирован
        User savedUser = userRepository.save(user);

        // создаём токен
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(savedUser);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        tokenRepository.save(verificationToken);

        // отправляем письмо
        sendVerificationEmail(savedUser.getEmail(), token);

        return savedUser;
    }

    private void sendVerificationEmail(String email, String token) {
        String link = "http://localhost:1001/verify?token=" + token;
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Activate your account");
        mailMessage.setText("Click the link to activate your account: " + link);
        mailSender.send(mailMessage);
    }

    public boolean verifyUser(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);

        if (verificationToken == null || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        return true;
    }


    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean phoneExists(String phone) {
        return userRepository.existsByPhone(phone);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }


    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email));
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public long countUsers() {
        return userRepository.countUsers();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public User findById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElse(null); // возвращаем null, если пользователь не найден
    }

    public User getById(Long id){
        return userRepository.findById(id).get();
    }

    public void updateUser(User user){
        userRepository.save(user);
    }

    public String getCurrentPassword(User user){
        return user.getPassword();
    }

    public void updatePassword(User user, String encodedPassword) {
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
}
