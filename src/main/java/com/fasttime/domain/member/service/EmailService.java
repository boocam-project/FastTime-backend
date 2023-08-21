package com.fasttime.domain.member.service;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailService {

    private final JavaMailSender emailsender; // Bean 등록해둔 MailConfig 를 emailsender 라는 이름으로 autowired
    private String authNum; // 인증번호

    //랜덤 인증 코드 생성
    public void createCode() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(3);

            switch (index) {
                case 0:
                    key.append((char) ((int) random.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char) ((int) random.nextInt(26) + 65));
                    break;
                case 2:
                    key.append(random.nextInt(9));
                    break;
            }
        }
        authNum = key.toString();
    }

    // 메일 양식 작성
    public MimeMessage createMessage(String to)
        throws MessagingException, UnsupportedEncodingException {
        createCode();
        String setFrom = "gjwldud0719@naver.com";
        String toEmail = to;
        String title = "회원가입 인증 번호";

        MimeMessage message = emailsender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, to); // 보내는 대상
        message.setSubject(title);
        message.setFrom(setFrom);
        String n = "";
        n += "<div style='margin:100px;'>";
        n += "<h1> 안녕하세요 FastTime 입니다.</h1>";
        n += "<p>아래 코드를 회원가입 창으로 돌아가 입력해주세요.<p>";
        n += "<br>";
        n += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        n += "<h3 style='color:#9AC1D1;'>회원가입 인증 코드</h3>";
        n += "<div style='font-size:130%'>";
        n += "CODE : <strong>";
        n += authNum + "</strong><div><br/> "; // 메일에 인증번호 넣기
        n += "</div>";

        message.setText(n, "utf-8", "html");
        return message;
    }


    // 메일 발송
    public String sendSimpleMessage(String to) throws Exception {
        createCode(); // 랜덤 인증코드 생성
        MimeMessage message = createMessage(to); // 메일 발송
        try {
            emailsender.send((message));
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        return authNum; // 메일로 보냈던 인증 코드를 서버로 반환
    }


}