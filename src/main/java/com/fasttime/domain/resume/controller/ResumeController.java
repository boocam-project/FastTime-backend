package com.fasttime.domain.resume.controller;

import com.fasttime.domain.resume.dto.ResumeDeleteServiceRequest;
import com.fasttime.domain.resume.dto.ResumeRequestDto;
import com.fasttime.domain.resume.dto.ResumeResponseDto;
import com.fasttime.domain.resume.dto.ResumeUpdateRequest;
import com.fasttime.domain.resume.dto.ResumeUpdateServiceRequest;
import com.fasttime.domain.resume.service.ResumeService;
import com.fasttime.global.util.ResponseDTO;
import com.fasttime.global.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/resumes")
public class ResumeController {

    private final SecurityUtil securityUtil;
    private final ResumeService resumeService;

    @PostMapping()
    public ResponseEntity<ResponseDTO<ResumeResponseDto>> createResume(
            @RequestBody @Valid ResumeRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDTO.res(HttpStatus.CREATED, "자기소개서가 등록되었습니다.",
                        resumeService.createResume(requestDto,
                                securityUtil.getCurrentMemberId())));
    }

    @DeleteMapping("/{resumeId}")
    public ResponseEntity<ResponseDTO<Void>> deleteResume(@PathVariable Long resumeId) {
        resumeService.delete(new ResumeDeleteServiceRequest(
                resumeId,
                securityUtil.getCurrentMemberId()
        ));
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDTO.res(HttpStatus.OK, null, null));
    }

    @PutMapping("/{resumeId}")
    public ResponseEntity<ResponseDTO<ResumeResponseDto>> updateResume(
            @PathVariable Long resumeId,
            @RequestBody @Valid ResumeUpdateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDTO.res(HttpStatus.OK, "자기소개서 업데이트 완료되었습니다.",
                        resumeService.updateResume(
                                new ResumeUpdateServiceRequest(resumeId,
                                        securityUtil.getCurrentMemberId(),
                                        request.title(),
                                        request.content()))));
    }

    @GetMapping("/{resumeId}")
    public ResponseEntity<ResponseDTO<ResumeResponseDto>> getResume(
            @PathVariable Long resumeId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDTO.res(HttpStatus.OK,
                        resumeService.getResume(resumeId)));
    }
}
