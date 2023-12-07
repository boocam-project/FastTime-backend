package com.fasttime.domain.member.controller;

import com.fasttime.domain.member.dto.request.EmailRequest;
import com.fasttime.domain.member.service.EmailService;
import com.fasttime.global.util.ResponseDTO;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/api/v1/confirm")
    public ResponseEntity<ResponseDTO<String>> mailConfirm(@RequestBody EmailRequest emailRequest)
        throws Exception {
        emailService.sendVerificationEmail(emailRequest.getEmail());
        return ResponseEntity.ok(
            ResponseDTO.res(HttpStatus.OK, "인증 이메일이 성공적으로 전송되었습니다.", null)
        );
    }

    @GetMapping("/api/v1/verify/{code}")
    public ResponseEntity<ResponseDTO<Boolean>> verifyEmail(
        @RequestParam("email") String email,
        @PathVariable("code") String code) {

        boolean isVerified = emailService.verifyEmailCode(email, code);
        if (isVerified) {
            return ResponseEntity.ok(ResponseDTO.res(HttpStatus.OK, "이메일 인증 성공.", true));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDTO.res(HttpStatus.BAD_REQUEST, "이메일 인증 실패.", false));
        }
    }
}