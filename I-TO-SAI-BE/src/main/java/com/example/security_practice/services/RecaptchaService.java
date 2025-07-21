package com.example.security_practice.services;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaService {
    private static final String VERIFY_URL =
            "https://www.google.com/recaptcha/api/siteverify";

    @Value("${recaptcha.secret}")
    private String secretKey;

    private final RestTemplate rest = new RestTemplate();

    public boolean verify(String token, String remoteIp) {
        MultiValueMap<String,String> form = new LinkedMultiValueMap<>();
        form.add("secret", secretKey);
        form.add("response", token);
        if (remoteIp != null) {
            form.add("remoteip", remoteIp);
        }

        RecaptchaResponse resp = rest.postForObject(
                VERIFY_URL,
                form,
                RecaptchaResponse.class
        );
        return resp != null && Boolean.TRUE.equals(resp.getSuccess());
    }

    @Getter @Setter
    public static class RecaptchaResponse {
        private Boolean success;
        private String hostname;
    }
}
