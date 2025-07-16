package com.example.security_practice.services;

import com.example.security_practice.entities.PasswordResetToken;
import com.example.security_practice.entities.User;
import com.example.security_practice.repositories.PasswordResetTokenRepository;
import com.example.security_practice.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PasswordResetService {
    private final UserRepository userRepo;
    private final PasswordResetTokenRepository tokenRepo;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public void initiatePasswordReset(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No account found for that email."));

        tokenRepo.deleteAllByUser(user);

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);

        PasswordResetToken prt = new PasswordResetToken();
        prt.setUser(user);
        prt.setToken(token);
        prt.setExpiresAt(expiry);
        tokenRepo.save(prt);

        String link = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/reset-password")
                .queryParam("token", token)
                .toUriString();

        String subj = "I‑TO‑SAI Password Reset";
        String htmlBody = String.format("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8" />
              <style>
                body { font-family: 'Segoe UI', Tahoma, sans-serif; background: #fafafa; margin:0; padding:0; }
                .container { max-width:600px; margin:2rem auto; background:#fff; border-radius:1rem; box-shadow:0 10px 25px rgba(0,0,0,0.1); overflow:hidden; }
                .header { background: linear-gradient(90deg, #f6ad55 0%%, #ed8936 100%%); color:#fff; padding:1.5rem; text-align:center; }
                .header h1 { margin:0; font-size:1.5rem; }
                .content { padding:2rem; color:#333; line-height:1.5; }
                .btn { display:inline-block; margin:2rem auto; padding:0.75rem 1.5rem; background: #ed8936; color:#fff; text-decoration:none; border-radius:0.5rem; font-weight:600; }
                .footer { padding:1rem; text-align:center; font-size:0.85rem; color:#888; }
              </style>
            </head>
            <body>
              <div class="container">
                <div class="header">
                  <h1>I‑TO‑SAI Password Reset</h1>
                </div>
                <div class="content">
                  <p>Hi <strong>%1$s</strong>,</p>
                  <p>We received a request to reset your password. Click the button below to choose a new one:</p>
                  <p style="text-align:center;">
                    <a href="%2$s" class="btn">Reset Password</a>
                  </p>
                  <p>If that button doesn’t work, copy and paste this link into your browser:</p>
                  <p><a href="%2$s">%2$s</a></p>
                  <p><em>This link expires in 1 hour.</em></p>
                </div>
                <div class="footer">
                  <p>With care,<br/>The I‑TO‑SAI Team</p>
                </div>
              </div>
            </body>
            </html>
            """, user.getUsername(), link);
        emailService.mailHtmlMessage(user.getEmail(), subj, htmlBody);
//        String body = "To reset your password, click:\n\n" + link
//                + "\n\nThis link expires in 1 hour.";
//        emailService.mailMessage(user.getEmail(), subj, body);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken prt = tokenRepo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        if (prt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expired");
        }

        User user = prt.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setEnabled(true);
        userRepo.save(user);

        tokenRepo.delete(prt);
    }
}

