package com.example.BookStore.controller;

import com.example.BookStore.entity.user.User;
import com.example.BookStore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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


    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("token") String token, Model model) {
        boolean verified = userService.verifyUser(token);
        if (verified) {
            model.addAttribute("message", "Account verified successfully! You can now login.");
            return "login";
        } else {
            model.addAttribute("message", "Invalid or expired token.");
            return "error"; // сделай страничку error.html
        }
    }

}
