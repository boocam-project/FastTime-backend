package com.fasttime.domain.member.controller;

import com.fasttime.domain.member.dto.request.EmailRequest;
import com.fasttime.domain.member.service.EmailService;
import com.fasttime.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final MemberService memberService;

    private final HttpSession session;

    @PostMapping("/api/v1/emailconfirm")
    public ResponseEntity<?> mailConfirm(@RequestBody EmailRequest emailRequest) throws Exception {
        try {

            if (!memberService.isEmailExistsInFcmember(emailRequest.getEmail())) {
                return ResponseEntity.badRequest()
                    .body("FastCampus에 등록된 이메일이 아닙니다.");
            }

            String code = emailService.sendSimpleMessage(emailRequest.getEmail());
            session.setAttribute("emailCode", code);
            return ResponseEntity.ok("success");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("메일 발송 실패: " + e.getMessage());
        }
    }


    @GetMapping("/api/v1/verify/{code}") // 이메일 인증하기 버튼
    public ResponseEntity<Map<String, Object>> verifyEmail(@PathVariable("code") String code,
        HttpSession session) {
        Map<String, Object> resultMap = new HashMap<>();
        session.setMaxInactiveInterval(30 * 60);

        String sessionCode = (String) session.getAttribute("emailCode");
        if (sessionCode != null && sessionCode.equals(code)) {
            // 인증 성공
            resultMap.put("success", true);
            return ResponseEntity.ok(resultMap);
        } else {
            // 인증 실패
            resultMap.put("success", false);
            return ResponseEntity.ok(resultMap);
        }
    }
}
