package com.example.BookStore.controller;

import com.example.BookStore.entity.User;
import com.example.BookStore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AppController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new User());
        return "signup_form";
    }

    @PostMapping("/process_register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result,Model model) {


        if (userService.emailExists(user.getEmail())) {
            result.rejectValue("email", null, "Email already registered");
        }

        if (userService.usernameExists(user.getUsername())) {
            result.rejectValue("username", null, "Username already taken");
        }

        if (userService.phoneExists(user.getPhone())) {
            result.rejectValue("phone", null, "Phone number already registered");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "signup_form";
        }

        userService.registerUser(user);
        return "redirect:/login";
    }


    @GetMapping("/login")
    public String login() {
        return "login"; // Thymeleaf-шаблон login.html
    }

}
