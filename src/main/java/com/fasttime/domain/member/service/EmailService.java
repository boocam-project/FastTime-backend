package com.fasttime.domain.member.service;

import com.fasttime.domain.member.exception.EmailSendingException;
import com.fasttime.domain.member.exception.EmailTemplateLoadException;
import jakarta.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailService {

    private final JavaMailSender javaMailSender;


    private String TEST_ID_EMAIL = "fasttime123@naver.com";

    private final Map<String, String> verificationCodes = new HashMap<>();

    public String sendVerificationEmail(String to) throws Exception {
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
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String emailTemplate = loadEmailTemplate("email_template.html");

        emailTemplate = emailTemplate.replace("{{authCode}}", authCode);

        helper.setSubject(title);
        helper.setFrom(new InternetAddress(setFrom, "boocam", "UTF-8"));
        helper.setTo(to);
        helper.setText(emailTemplate, true);

        return message;
    }

    private String loadEmailTemplate(String templateName) {
        try {
            Resource resource = new ClassPathResource("templates/" + templateName);
            InputStream inputStream = resource.getInputStream();
            byte[] templateBytes = inputStream.readAllBytes();
            return new String(templateBytes, "UTF-8");
        } catch (IOException ex) {
            throw new EmailTemplateLoadException();
        }
    }
}
