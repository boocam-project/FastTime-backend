package com.fasttime.domain.member.controller;

import com.fasttime.domain.member.request.CodeRequest;
import com.fasttime.domain.member.request.EmailRequest;
import com.fasttime.domain.member.service.EmailService;
import com.fasttime.global.util.ResponseDTO;
import javax.naming.AuthenticationException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    private final HttpSession session;

    @PostMapping("v1/emailconfirm") // 인증번호 발송 버튼 누르면 메일 가게
    public ResponseEntity<String> mailConfirm(@RequestBody EmailRequest emailRequest)
        throws Exception {
        String code = emailService.sendSimpleMessage(emailRequest.getEmail());
        session.setAttribute("emailCode", code);

        return ResponseEntity.ok("success");
    }


    @GetMapping("v1/verify/{code}") // 이메일 인증하기 버튼
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

    // 인증번호 발송 버튼 누르면 메일 가는 메소드 (Response에 이메일 추가를 위해 다시만들었습니다.)
    @PostMapping("v1/Repassword/emailconfirm")
    public ResponseEntity<ResponseDTO> mailConfirmForRePassword
    (@RequestBody EmailRequest emailRequest) throws Exception {
        String code = emailService.sendSimpleMessage(emailRequest.getEmail());
        session.setAttribute("emailCode", code);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.res
            (HttpStatus.OK,"이메일을 성공적으로 보냈습니다.",emailRequest.getEmail()));
    }

    @PostMapping("v1/RePassword/verify") // 비밀번호 재설정을 위한 코드 받기
    public ResponseEntity<ResponseDTO> verifyMember(@RequestBody CodeRequest request
        , HttpSession session) throws AuthenticationException {

        session.setMaxInactiveInterval(30 * 60);
        String sessionCode = (String) session.getAttribute("emailCode");

        if (sessionCode != null && sessionCode.equals(request.getCode())) {
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.res
                (HttpStatus.OK, "코드 검증이 완료되었습니다.", request.getEmail()));
        } else {
            // 인증 실패
            throw new AuthenticationException();
        }
    }
}