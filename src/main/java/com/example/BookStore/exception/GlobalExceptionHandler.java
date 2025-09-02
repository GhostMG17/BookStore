package com.example.BookStore.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Ловим любую ошибку
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllExceptions(Exception ex) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("message", ex.getMessage()); // сообщение ошибки
        mav.setViewName("error/custom-error"); // html-шаблон для отображения
        return mav;
    }
}
