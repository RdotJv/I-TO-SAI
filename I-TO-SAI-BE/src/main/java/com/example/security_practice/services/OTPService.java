//package com.example.security_practice.services;
//
//import com.example.security_practice.entities.OTPToken;
//import com.example.security_practice.entities.User;
//import com.example.security_practice.repositories.OTPTokenRepository;
//import com.example.security_practice.repositories.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.security.SecureRandom;
//import java.time.LocalDateTime;
//import java.util.Random;
//
//@Service
//@RequiredArgsConstructor
//public class OTPService {
//    private final OTPTokenRepository otpRepo;
//    private final EmailService emailService;  // from our JavaMailSender setup
//    private final UserRepository userRepo;
//    private final Random random = new SecureRandom();
//
//    public void createAndSendOtp(String username) {
//        User user = userRepo.findByUsername(username)
//                .orElseThrow();
//        String code = String.format("%06d", random.nextInt(1_000_000));
//        LocalDateTime expires = LocalDateTime.now().plusMinutes(10);
//
//        otpRepo.findByUserUsername(username)
//                .ifPresent(otpRepo::delete);
//
//        OTPToken token = new OTPToken();
//        token.setUser(user);
//        token.setCode(code);
//        token.setExpiresAt(expires);
//        otpRepo.save(token);
//
//        String subject = "I-TO-SAI Verification Code";
//        String body = "Welcome to I-TO-SAI! Your OTP code is: "+code+"\nIt will expire in 10 minutes.";
//        emailService.mailMessage(user.getEmail(), subject, body);
//    }
//
//    public boolean verifyOtp(String username, String code) {
//        OTPToken token = otpRepo.findByUserUsername(username)
//                .orElseThrow();
//        if (token.getExpiresAt().isBefore(LocalDateTime.now())) return false;
//        if (!token.getCode().equals(code)) return false;
//
//        User user = token.getUser();
//        user.setEnabled(true);
//        userRepo.save(user);
//
//        otpRepo.delete(token);
//        return true;
//    }
//}
