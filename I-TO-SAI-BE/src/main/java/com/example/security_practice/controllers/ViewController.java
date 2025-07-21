package com.example.security_practice.controllers;

import com.example.security_practice.entities.EmailVerificationToken;
import com.example.security_practice.entities.User;
import com.example.security_practice.repositories.EmailVerificationTokenRepository;
import com.example.security_practice.repositories.UserRepository;
import com.example.security_practice.services.RecaptchaService;
import com.example.security_practice.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
@AllArgsConstructor
public class ViewController {
    private final UserService userService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepo;
    private final UserRepository userRepo;
    private final RecaptchaService recaptchaService;

    @Value("${recaptcha.sitekey}")
    private String siteKey;

    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/home")
    public String home() {
        return "home";
    }
    @GetMapping("/register")
    public String showForm(Model model) {
        model.addAttribute("siteKey", siteKey);
        return "register";
    }


    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token) {
        var opt = emailVerificationTokenRepo.findByToken(token);
        if (opt.isEmpty()) {
            return "redirect:/register?fail_invalid";
        }

        EmailVerificationToken vtok = opt.get();
        if (vtok.getExpiresAt().isBefore(LocalDateTime.now())) {
            return "redirect:/register?fail_expire";
        }

        User user = vtok.getUser();
        user.setEnabled(true);
        userRepo.save(user);

        emailVerificationTokenRepo.delete(vtok);

        return "redirect:/login?registered";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam("g-recaptcha-response") String captchaToken,
            HttpServletRequest request,
            Model model
    ) {
        String clientIp = request.getRemoteAddr();
        if (!recaptchaService.verify(captchaToken, clientIp)) {
            model.addAttribute("error", "CAPTCHA verification failed. Please try again.");
            model.addAttribute("siteKey", siteKey);
            return "register";
        }
        try {
            userService.register(username, password, email);
            return "redirect:/register?sent";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
    	    model.addAttribute("siteKey", siteKey);
            return "register";
        }
    }
}
