package com.example.security_practice.controllers;

import com.example.security_practice.services.PasswordResetService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class PasswordController {
    private final PasswordResetService resetService;

    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String submitForgotPassword(@RequestParam String email, Model m) {
        resetService.initiatePasswordReset(email);
        m.addAttribute("sent", true);
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam String token, Model m) {
        m.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String submitResetPassword(
            @RequestParam String token,
            @RequestParam String password,
            Model m) {
        resetService.resetPassword(token, password);
        return "redirect:/login?resetSuccess";
    }
}

