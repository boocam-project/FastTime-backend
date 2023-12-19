package com.fasttime.domain.member.service;

import com.fasttime.domain.member.exception.EmailSendingException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Profile("local || develop")
@Service
@Transactional
public class LocalEmailService implements EmailUseCase {

    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    @Override
    public String sendVerificationEmail(String to) {
        String authCode = generateAuthCode();
        try {
            log.info("send verification email...");
            log.info("target: {}", to);
            log.info("code: {}", authCode);
            verificationCodes.put(to, authCode);
            return authCode;
        } catch (MailException ex) {
            throw new EmailSendingException();
        }
    }

    @Override
    public boolean verifyEmailCode(String email, String code) {
        String storedCode = verificationCodes.get(email);
        return storedCode != null && storedCode.equals(code);
    }

    private String generateAuthCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(3);
            switch (index) {
                case 0:
                    key.append((char) (random.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char) (random.nextInt(26) + 65));
                    break;
                case 2:
                    key.append(random.nextInt(10));
                    break;
            }
        }
        return key.toString();
    }
}