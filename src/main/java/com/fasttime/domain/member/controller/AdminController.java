package com.fasttime.domain.member.controller;

import com.fasttime.domain.member.service.AdminService;
import java.util.HashMap;
import java.util.Map;
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
    public ResponseEntity<Map<String, Object>> PostList() {
        Map<String, Object> message = new HashMap<>();
        message.put("status", 200);
        message.put("data", adminService.FindReportedPost());
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @GetMapping("/admin/{post_id}")
    public ResponseEntity<Map<String, Object>> PostDetail
        (@PathVariable("post_id") Long post_id) {
        Map<String, Object> message = new HashMap<>();
        message.put("status", 200);
        message.put("data", adminService.FindOneReportedPost(post_id));
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @GetMapping("/admin/{post_id}/delete") // 문제가 있는 Post를 삭제
    public ResponseEntity<Map<String, Object>> DeletePost
        (@PathVariable("post_id") Long post_id) {
        Map<String, Object> message = new HashMap<>();
        message.put("status", 200);
        adminService.DeletePost(post_id);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @GetMapping("/admin/{post_id}/pass") // 문제가 없는 Post 검토완료로 변경
    public ResponseEntity<Map<String, Object>> PassPost
        (@PathVariable("post_id") Long post_id) {
        Map<String, Object> message = new HashMap<>();
        message.put("status", 200);
        adminService.PassPost(post_id);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
}
