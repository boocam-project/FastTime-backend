package com.fasttime.domain.member.controller;

import com.fasttime.domain.member.service.AdminService;
import com.fasttime.global.util.ResponseDTO;
import java.rmi.AccessException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/admin")
    public ResponseEntity<ResponseDTO> PostList() {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.res(HttpStatus.OK
            ,"신고가 10번이상된 게시글들을 보여줍니다.",adminService.FindReportedPost()));
    }

    @GetMapping("/admin/{post_id}")
    public ResponseEntity<ResponseDTO> PostDetail
        (@PathVariable("post_id") Long post_id) throws AccessException {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.res(HttpStatus.OK
            ,"신고가 10번이상된 게시글을 보여줍니다.",adminService.FindOneReportedPost(post_id)));
    }

    @GetMapping("/admin/{post_id}/delete") // 문제가 있는 Post를 삭제
    public ResponseEntity<ResponseDTO> DeletePost
        (@PathVariable("post_id") Long post_id) throws AccessException {
        adminService.DeletePost(post_id);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.res(HttpStatus.OK
            , "신고가 10번이상된 게시글을 삭제합니다."));
    }

    @GetMapping("/admin/{post_id}/pass") // 문제가 없는 Post 검토완료로 변경
    public ResponseEntity<ResponseDTO> PassPost
        (@PathVariable("post_id") Long post_id) throws AccessException {
        adminService.PassPost(post_id);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.res(HttpStatus.OK
            , "신고가 10번이상된 게시글을 복구합니다."));
    }
}
