package com.fasttime.domain.member.controller;

import com.fasttime.domain.member.dto.request.LoginRequestDTO;
import com.fasttime.domain.member.dto.request.saveAdminDTO;
import com.fasttime.domain.member.service.AdminService;
import com.fasttime.global.util.ResponseDTO;
import java.rmi.AccessException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<ResponseDTO> postList() {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.res(HttpStatus.OK
            , "신고가 10번이상된 게시글들을 보여줍니다.", adminService.findReportedPost()));
    }

    @GetMapping("/{post_id}")
    public ResponseEntity<ResponseDTO> postDetail
        (@PathVariable("post_id") Long post_id) throws AccessException {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.res(HttpStatus.OK
            , "신고가 10번이상된 게시글을 보여줍니다.", adminService.findOneReportedPost(post_id)));
    }

    @GetMapping("/{post_id}/delete") // 문제가 있는 Post를 삭제
    public ResponseEntity<ResponseDTO> deletePost
        (@PathVariable("post_id") Long post_id) throws AccessException {
        adminService.deletePost(post_id);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.res(HttpStatus.OK
            , "신고가 10번이상된 게시글을 삭제합니다."));
    }

    @GetMapping("/{post_id}/pass") // 문제가 없는 Post 검토완료로 변경
    public ResponseEntity<ResponseDTO> passPost
        (@PathVariable("post_id") Long post_id) throws AccessException {
        adminService.passPost(post_id);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.res(HttpStatus.OK
            , "신고가 10번이상된 게시글을 복구합니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto,
        HttpSession session) {
        if (session.getAttribute("MEMBER") != null) {
            session.removeAttribute("MEMBER");
        }
        session.setAttribute("ADMIN",adminService.loginAdmin(dto));
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.res
            (HttpStatus.OK, "관리자 로그인 완료"));
    }
    @PostMapping("/join")
    public ResponseEntity<ResponseDTO> join(@RequestBody @Valid saveAdminDTO dto) {
        adminService.save(dto);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.res
            (HttpStatus.OK, "관리자 회원가입 완료"));
    }
}
