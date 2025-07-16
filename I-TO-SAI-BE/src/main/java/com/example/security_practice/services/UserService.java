package com.example.security_practice.services;

import com.example.security_practice.entities.EmailVerificationToken;
import com.example.security_practice.repositories.EmailVerificationTokenRepository;
import com.example.security_practice.repositories.UserRepository;
import com.example.security_practice.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationTokenRepository emailVerificationTokenRepo;
    private final EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail)
            throws UsernameNotFoundException {
        Optional<User> optUser = userRepo.findByUsername(usernameOrEmail);
        if (optUser.isEmpty()) {
            optUser = userRepo.findByEmail(usernameOrEmail);
        }
        User user = optUser.orElseThrow(()->new UsernameNotFoundException("no user found w email/password: "+ usernameOrEmail));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                user.getRoles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet())
        );
    }

    public void register(String username, String rawPassword, String email) {
        if (userRepo.findByUsername(username).isPresent() || userRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Username or Email already in use.");
        }
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(rawPassword));
        newUser.setEmail(email);
        newUser.setRoles(Set.of("ROLE_USER"));
        newUser.setEnabled(false);
        userRepo.save(newUser);

        String token = UUID.randomUUID().toString();
        LocalDateTime expire = LocalDateTime.now().plusDays(1);

        EmailVerificationToken vtok = new EmailVerificationToken();
        vtok.setUser(newUser);
        vtok.setToken(token);
        vtok.setExpiresAt(expire);
        emailVerificationTokenRepo.save(vtok);

        String link = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/verify-email")
                .queryParam("token", token)
                .toUriString();

        String subj = "I-TO-SAI Email Confirmation";
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
                  <h1>I‑TO‑SAI Email Confirmation</h1>
                </div>
                <div class="content">
                  <p>Hi <strong>%1$s</strong>,</p>
                  <p>Thank you for signing up! To activate your account, please click the button below:</p>
                  <p style="text-align:center;">
                    <a href="%2$s" class="btn">Confirm Your Email</a>
                  </p>
                  <p>If the button doesn’t work, copy and paste this URL into your browser:</p>
                  <p><a href="%2$s">%2$s</a></p>
                  <p>This link will expire in 24 hours.</p>
                </div>
                <div class="footer">
                  <p>With love,<br/>I‑TO‑SAI Team</p>
                </div>
              </div>
            </body>
            </html>
            """, username, link);


//        String body = "Click this link to activate your account:\n\n" + link +
//                "\n\nLink expires in 24 hours.";
//        emailService.mailMessage(newUser.getEmail(), subj, body);
        emailService.mailHtmlMessage(newUser.getEmail(), subj, htmlBody);
    }
}
