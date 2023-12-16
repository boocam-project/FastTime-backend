package com.fasttime.domain.member.service;

import com.fasttime.domain.member.exception.EmailSendingException;
import com.fasttime.domain.member.exception.EmailTemplateLoadException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile("prod")
@RequiredArgsConstructor
@Transactional
public class ProdEmailService implements EmailUseCase{

    private final JavaMailSender javaMailSender;

    private static final String TEST_ID_EMAIL = "fasttime123@naver.com";

    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    @Override
    public String sendVerificationEmail(String to)
        throws MessagingException, UnsupportedEncodingException {
        String authCode = generateAuthCode();
        MimeMessage message = createMessage(to, authCode);
        try {
            javaMailSender.send(message);
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

    private MimeMessage createMessage(String to, String authCode)
        throws MessagingException, UnsupportedEncodingException {
        String setFrom = TEST_ID_EMAIL;
        String title = "회원가입 인증 번호";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        String emailTemplate = loadEmailTemplate("email_template.html");

        emailTemplate = emailTemplate.replace("{{authCode}}", authCode);

        helper.setSubject(title);
        helper.setFrom(new InternetAddress(setFrom, "boocam", StandardCharsets.UTF_8.name()));
        helper.setTo(to);
        helper.setText(emailTemplate, true);

        return message;
    }

    private String loadEmailTemplate(String templateName) {
        try {
            Resource resource = new ClassPathResource("templates/" + templateName);
            InputStream inputStream = resource.getInputStream();
            byte[] templateBytes = inputStream.readAllBytes();
            return new String(templateBytes, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new EmailTemplateLoadException();
        }
    }
}
